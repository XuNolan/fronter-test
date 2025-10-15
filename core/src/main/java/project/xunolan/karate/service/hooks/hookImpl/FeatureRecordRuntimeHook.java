package project.xunolan.karate.service.hooks.hookImpl;

import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.FeatureRuntime;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.core.Step;
import com.intuit.karate.core.StepResult;
import lombok.extern.slf4j.Slf4j;
import project.xunolan.database.entity.Record;
import project.xunolan.database.repository.RecordRepository;
import project.xunolan.database.entity.ExecuteLogRecordRelated;
import project.xunolan.database.repository.ExecuteLogRecordRelatedRepository;
import project.xunolan.karate.service.FeatureStartService;
import project.xunolan.service.ScreenRecorderService;
import project.xunolan.websocket.utils.BeanUtils;
import project.xunolan.service.SessionKeyEnum;
import com.alibaba.fastjson.JSON;

import javax.websocket.Session;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Feature 录制切面回调
 * 以 Feature 为单元管理屏幕录制生命周期
 */
@Slf4j
public class FeatureRecordRuntimeHook implements RuntimeHook {

    private ScreenRecorderService screenRecorderService;

    private ScreenRecorderService getScreenRecorderService() {
        if (screenRecorderService == null) {
            screenRecorderService = BeanUtils.getBean(ScreenRecorderService.class);
        }
        return screenRecorderService;
    }

    /**
     * Feature 开始前：启动录制，并预分配 record ID
     */
    @Override
    public boolean beforeFeature(FeatureRuntime fr) {
        Session session = FeatureStartService.currentlyUseSession.get();
        Long scriptId = SessionKeyEnum.SCRIPT_ID.get(session);
        Long usecaseId = SessionKeyEnum.USECASE_ID.get(session);
        Long executeGroupId = SessionKeyEnum.EXECUTE_GROUP_ID.getOrDefault(session, 0L);
        
        SessionKeyEnum.RECORD_FAIL.set(session, false);
        SessionKeyEnum.RECORD_START_TIME.set(session, System.currentTimeMillis());
        
        // 预分配 record 表的 ID（创建一个临时记录）
        RecordRepository recordRepository = BeanUtils.getBean(RecordRepository.class);
        
        // 获取录制配置信息
        ScreenRecorderService.RecordingRuntimeConfig config = getScreenRecorderService().loadRuntimeConfig();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("format", config.format);
        metadata.put("frameRate", config.frameRate);
        metadata.put("quality", config.quality);
        metadata.put("qualityPreset", config.qualityPreset);
        metadata.put("storage", config.storage);
        metadata.put("duration", 0);
        metadata.put("file_size", 0);
        metadata.put("file_name", "");
        metadata.put("created_at", System.currentTimeMillis());
        
        Record record = Record.builder()
                .usecaseId(usecaseId)
                .scriptId(scriptId)
                .executeGroupId(executeGroupId)
                .status(2) // 2: 执行中止（初始状态）
                .storageType(getScreenRecorderService().getStorageType())
                .recordConfigType(getRecordConfigType())
                .executeTime(0)
                .metadata(JSON.toJSONString(metadata))
                .build();
        
        record = recordRepository.save(record);
        Long recordId = record.getId();
        SessionKeyEnum.RECORD_ID.set(session, recordId);
        
        log.info("Pre-allocated record ID: {} for script: {}", recordId, scriptId);
        
        // 启动录制
        String recordFileName = null;
        try {
            recordFileName = getScreenRecorderService().startRecording(scriptId, usecaseId);
            SessionKeyEnum.RECORD_FILE_NAME.set(session, recordFileName);
        } catch (Exception e) {
            log.error("Failed to start recording", e);
            try {
                if (getScreenRecorderService().isRecording()){
                    getScreenRecorderService().stopRecording();
                }
                SessionKeyEnum.RECORD_FAIL.set(session, true);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        
        return true;
    }
    
    /**
     * Feature 结束后：停止录制并更新 record 表
     */
    @Override
    public void afterFeature(FeatureRuntime fr) {
        Session session = FeatureStartService.currentlyUseSession.get();
        Long recordId = SessionKeyEnum.RECORD_ID.get(session);
        Long executeLogId = SessionKeyEnum.EXECUTE_LOG_ID.get(session);
        Long recordStartTime = SessionKeyEnum.RECORD_START_TIME.get(session);
        Boolean recordFail = SessionKeyEnum.RECORD_FAIL.getOrDefault(session, false);
        
        if (recordId == null) {
            log.warn("No record ID found in session");
            return;
        }
        
        RecordRepository recordRepository = BeanUtils.getBean(RecordRepository.class);
        
        try {
            // 停止录制，获取文件
            File recordingFile = null;
            if (!recordFail) {
                recordingFile = getScreenRecorderService().stopRecording();
                if (recordingFile != null) {
                    log.info("Feature recording completed: {}", recordingFile.getAbsolutePath());
                }
            }
            
            // 计算执行时间
            int executeTime = recordStartTime != null ? 
                    (int) (System.currentTimeMillis() - recordStartTime) : 0;
            
            // 获取录制配置信息
            ScreenRecorderService.RecordingRuntimeConfig config = getScreenRecorderService().loadRuntimeConfig();
            
            // 获取并更新 record 记录
            Record record = recordRepository.findById(recordId).orElse(null);
            if (record != null) {
                // 仅记录录制结果状态：0 录制成功 / 1 录制失败
                // 脚本执行状态（成功/失败）由 execute_log 负责记录，互不影响
                record.setStatus(recordFail ? 1 : 0);
                record.setExecuteTime(executeTime);
                
                // 根据存储类型处理录制文件
                String storageType = getScreenRecorderService().getStorageType();
                if ("db".equals(storageType) && recordingFile != null) {
                    // 存储到数据库中
                    byte[] fileData = Files.readAllBytes(recordingFile.toPath());
                    record.setRecordData(fileData);
                    record.setRecordUrl(recordingFile.getName());
                    log.info("Saved recording to database, size: {} bytes", fileData.length);
                } else if ("local".equals(storageType) && recordingFile != null) {
                    // 存储到本地，只记录文件路径
                    record.setRecordUrl(recordingFile.getAbsolutePath());
                    log.info("Saved recording path to database: {}", recordingFile.getAbsolutePath());
                }
                
                // 更新元数据
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("format", config.format);
                metadata.put("frameRate", config.frameRate);
                metadata.put("quality", config.quality);
                metadata.put("qualityPreset", config.qualityPreset);
                metadata.put("storage", config.storage);
                metadata.put("duration", executeTime);
                metadata.put("file_size", recordingFile != null ? recordingFile.length() : 0);
                metadata.put("file_name", recordingFile != null ? recordingFile.getName() : "");
                metadata.put("created_at", recordStartTime != null ? recordStartTime : System.currentTimeMillis());
                metadata.put("file_extension", recordingFile != null ? getFileExtension(recordingFile.getName()) : "");
                
                record.setMetadata(JSON.toJSONString(metadata));
                
                
                recordRepository.save(record);
                
                // 如果存在 execute_log_id，建立一对一关联
                if (executeLogId != null) {
                    ExecuteLogRecordRelatedRepository relRepo = BeanUtils.getBean(ExecuteLogRecordRelatedRepository.class);
                    ExecuteLogRecordRelated rel = ExecuteLogRecordRelated.builder()
                            .executeLogId(executeLogId)
                            .recordId(record.getId())
                            .build();
                    relRepo.save(rel);
                }
                log.info("Updated record ID: {} with recording info", recordId);
            }

        } catch (Exception e) {
            log.error("Failed to save recording info for record ID: {}", recordId, e);
            // 标记为失败状态
            recordRepository.findById(recordId).ifPresent(record -> {
                record.setStatus(1); // 1: 执行失败
                recordRepository.save(record);
            });
        }
    }
    
    /**
     * Scenario 开始前：记录日志
     */
    @Override
    public boolean beforeScenario(ScenarioRuntime sr) {
        log.debug("Recording scenario: {}", sr.scenario.getName());
        return true;
    }
    
    /**
     * Step 开始前：记录日志
     */
    @Override
    public boolean beforeStep(Step step, ScenarioRuntime sr) {
        log.debug("Recording step: {}", step.getText());
        return true;
    }
    
    /**
     * Step 结束后：记录日志
     */
    @Override
    public void afterStep(StepResult stepResult, ScenarioRuntime sr) {
        log.debug("Step completed: {}", stepResult.getStep().getText());
        //脚本执行失败与录制操作无关。
    }
    
    /**
     * 获取录制配置类型
     */
    private int getRecordConfigType() {
        ScreenRecorderService.RecordingRuntimeConfig config = getScreenRecorderService().loadRuntimeConfig();
        String preset = config.qualityPreset != null ? config.qualityPreset : "medium";
        
        switch (preset.toLowerCase()) {
            case "high":
                return 2; // HIGH
            case "low":
                return 0; // LOW
            case "medium":
            default:
                return 1; // MEDIUM/BALANCE
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
}
