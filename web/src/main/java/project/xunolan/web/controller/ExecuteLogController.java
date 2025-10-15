package project.xunolan.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.xunolan.database.entity.ExecuteLog;
import project.xunolan.database.entity.ExecuteLogRecordRelated;
import project.xunolan.database.entity.ExecuteGroupId;
import project.xunolan.database.entity.Record;
import project.xunolan.database.repository.ExecuteLogRecordRelatedRepository;
import project.xunolan.database.repository.ExecuteLogRepository;
import project.xunolan.database.repository.RecordRepository;
import project.xunolan.database.repository.ExecuteGroupIdRepository;
import project.xunolan.web.amisEntity.aspect.AmisResult;
import project.xunolan.service.ScriptService;
import project.xunolan.service.VideoConversionService;
import project.xunolan.database.entity.Script;
import project.xunolan.web.amisEntity.entity.AmisCrudListVo;
import project.xunolan.web.amisEntity.utils.Convert4Amis;
import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import java.nio.file.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/execute-log")
public class ExecuteLogController {

    @Autowired
    private ExecuteLogRepository executeLogRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private ExecuteLogRecordRelatedRepository executeLogRecordRelatedRepository;

    @Autowired
    private ScriptService scriptService;

    @Autowired
    private ExecuteGroupIdRepository executeGroupIdRepository;
    
    @Autowired
    private VideoConversionService videoConversionService;

    /**
     * 查询指定脚本的执行日志列表（带录制信息）
     */
    @AmisResult
    @GetMapping("/list")
    public AmisCrudListVo getExecuteLogsByScriptId(@RequestParam("scriptId") Long scriptId) {
        // 1. 查询该脚本的所有执行日志
        List<ExecuteLog> executeLogs = executeLogRepository.findByScriptIdOrderByIdDesc(scriptId);

        // 2. 获取所有执行日志ID
        List<Long> executeLogIds = executeLogs.stream()
                .map(ExecuteLog::getId)
                .collect(Collectors.toList());

        // 3. 查询关联的录制记录
        Map<Long, ExecuteLogRecordRelated> relatedMap = new HashMap<>();
        if (!executeLogIds.isEmpty()) {
            List<ExecuteLogRecordRelated> relations = executeLogRecordRelatedRepository
                    .findByExecuteLogIdIn(executeLogIds);
            for (ExecuteLogRecordRelated rel : relations) {
                relatedMap.put(rel.getExecuteLogId(), rel);
            }
        }

        // 4. 查询所有录制记录
        List<Long> recordIds = relatedMap.values().stream()
                .map(ExecuteLogRecordRelated::getRecordId)
                .collect(Collectors.toList());
        
        Map<Long, Record> recordMap = new HashMap<>();
        if (!recordIds.isEmpty()) {
            List<Record> records = recordRepository.findAllById(recordIds);
            for (Record record : records) {
                recordMap.put(record.getId(), record);
            }
        }

        // 5. 组装VO
        List<Map<String, Object>> result = new ArrayList<>();
        for (ExecuteLog executeLog : executeLogs) {
            Map<String, Object> item = new HashMap<>();
            item.put("executeLogId", executeLog.getId());
            item.put("created", executeLog.getCreated());
            item.put("executeTime", executeLog.getExecuteTime());
            item.put("status", executeLog.getStatus());
            item.put("statusText", getStatusText(executeLog.getStatus()));
            
            // 关联的录制信息
            ExecuteLogRecordRelated relation = relatedMap.get(executeLog.getId());
            if (relation != null) {
                Record record = recordMap.get(relation.getRecordId());
                if (record != null) {
                    item.put("hasRecord", true);
                    item.put("recordId", record.getId());
                    item.put("recordStatus", record.getStatus());
                    item.put("recordStatusText", getStatusText(record.getStatus()));
                    item.put("recordStorageType", record.getStorageType());
                } else {
                    item.put("hasRecord", false);
                }
            } else {
                item.put("hasRecord", false);
            }
            
            // 执行组名
            if (executeLog.getExecuteGroupId() != null) {
                item.put("executeGroupId", executeLog.getExecuteGroupId());
                ExecuteGroupId group = executeGroupIdRepository.findById(executeLog.getExecuteGroupId()).orElse(null);
                if (group != null) {
                    item.put("executeGroupName", group.getGroupName());
                }
            }
            result.add(item);
        }

        return AmisCrudListVo.builder()
                .total(result.size())
                .items(result)
                .build();
    }

    /**
     * 获取执行日志详情
     */
    @AmisResult
    @GetMapping("/detail/{id}")
    public Map<String, Object> getExecuteLogDetail(@PathVariable("id") Long id) {
        ExecuteLog executeLog = executeLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("执行日志不存在"));

        // 直接构建结果，避免 Convert4Amis 的字段前缀问题
        Map<String, Object> result = new HashMap<>();
        result.put("id", executeLog.getId());
        result.put("scriptId", executeLog.getScriptId());
        result.put("logData", executeLog.getLogData());
        result.put("status", executeLog.getStatus());
        result.put("executeTime", executeLog.getExecuteTime());
        result.put("created", executeLog.getCreated());
        result.put("statusText", getStatusText(executeLog.getStatus()));

        // 查找关联的录制记录
        ExecuteLogRecordRelated relation = executeLogRecordRelatedRepository
                .findByExecuteLogId(id).orElse(null);
        
        if (relation != null) {
            Record record = recordRepository.findById(relation.getRecordId()).orElse(null);
            if (record != null) {
                result.put("hasRecord", true);
                result.put("recordId", record.getId());
                result.put("recordStatus", record.getStatus());
                result.put("recordStatusText", getStatusText(record.getStatus()));
                result.put("recordStorageType", record.getStorageType());
                result.put("recordUrl", record.getRecordUrl());
            } else {
                result.put("hasRecord", false);
            }
        } else {
            result.put("hasRecord", false);
        }

        return result;
    }

    /**
     * 获取录制记录详情
     */
    @AmisResult
    @GetMapping("/record/{id}")
    public Map<String, Object> getRecordDetail(@PathVariable("id") Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("录制记录不存在"));

        Map<String, Object> result = Convert4Amis.flatSingleMapWithPrefix("", record);
        result.put("statusText", getStatusText(record.getStatus()));
        
        return result;
    }

    /**
     * 删除：日志、录制或两者
     */
    @AmisResult
    @PostMapping("/delete")
    @Transactional
    public Map<String, Object> delete(@RequestParam(value = "executeLogId", required = false) Long executeLogId,
                                      @RequestParam(value = "deleteRecord", defaultValue = "false") Boolean deleteRecord,
                                      @RequestParam(value = "deleteLog", defaultValue = "true") Boolean deleteLog,
                                      @RequestBody(required = false) Map<String, Object> requestBody) {
        
        // 优先从请求体获取参数，如果没有则使用 URL 参数
        if (requestBody != null) {
            executeLogId = executeLogId != null ? executeLogId : 
                (requestBody.get("executeLogId") != null ? Long.valueOf(requestBody.get("executeLogId").toString()) : null);
            deleteRecord = deleteRecord != null ? deleteRecord : 
                (requestBody.get("deleteRecord") != null ? Boolean.valueOf(requestBody.get("deleteRecord").toString()) : false);
            deleteLog = deleteLog != null ? deleteLog : 
                (requestBody.get("deleteLog") != null ? Boolean.valueOf(requestBody.get("deleteLog").toString()) : true);
        }
        
        if (executeLogId == null) {
            Map<String, Object> errorResp = new HashMap<>();
            errorResp.put("status", 1);
            errorResp.put("msg", "executeLogId 参数不能为空");
            return errorResp;
        }
        
        return deleteExecuteLogAndRecord(executeLogId, deleteRecord, deleteLog);
    }


    /**
     * 实际的删除逻辑
     */
    private Map<String, Object> deleteExecuteLogAndRecord(Long executeLogId, Boolean deleteRecord, Boolean deleteLog) {
        Map<String, Object> resp = new HashMap<>();
        boolean removedRelation = false;
        boolean deletedRecord = false;
        boolean deletedLog = false;

        // 仅支持两种模式：只删录制 或 两者都删
        if (!deleteRecord && deleteLog) {
            // 不允许只删日志而保留录制，避免孤儿数据
            deleteRecord = true;
        }
        
        // 转换为 boolean 类型
        boolean deleteRecordBool = deleteRecord != null ? deleteRecord : false;
        boolean deleteLogBool = deleteLog != null ? deleteLog : true;

        // 先处理关联 & 录制删除
        Optional<ExecuteLogRecordRelated> optionalRel = executeLogRecordRelatedRepository.findByExecuteLogId(executeLogId);
        if (optionalRel.isPresent()) {
            ExecuteLogRecordRelated rel = optionalRel.get();
            if (deleteRecordBool) {
                // 可选：若为本地存储，尝试删除文件
                recordRepository.findById(rel.getRecordId()).ifPresent(record -> {
                    if ("local".equalsIgnoreCase(record.getStorageType())) {
                        String path = record.getRecordUrl();
                        if (path != null) {
                            try { Files.deleteIfExists(Paths.get(path)); } catch (Exception ignored) {}
                        }
                    }
                });
                recordRepository.deleteById(rel.getRecordId());
                deletedRecord = true;
            }
            // 删除或修改其中任意一方，都需要清理关联
            executeLogRecordRelatedRepository.delete(rel);
            removedRelation = true;
        }

        // 处理日志（仅当全部删除时）
        if (deleteLogBool) {
            if (executeLogRepository.existsById(executeLogId)) {
                executeLogRepository.deleteById(executeLogId);
                deletedLog = true;
            }
        }

        resp.put("status", "ok");
        resp.put("deletedLog", deletedLog);
        resp.put("deletedRecord", deletedRecord);
        resp.put("removedRelation", removedRelation);
        return resp;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0:
                return "成功";
            case 1:
                return "失败";
            case 2:
                return "中止/进行中";
            default:
                return "未知";
        }
    }

    /**
     * 获取执行日志对应的录制视频文件
     * @param executeLogId 执行日志ID
     * @return 视频文件的二进制数据
     */
    @GetMapping("/video/{executeLogId}")
    public ResponseEntity<byte[]> getRecordingVideo(@PathVariable("executeLogId") Long executeLogId) {
        try {
            log.info("开始获取执行日志 {} 的录制视频", executeLogId);
            
            // 1. 通过 executeLogId 查找关联的 recordId
            ExecuteLogRecordRelated relation = executeLogRecordRelatedRepository
                    .findByExecuteLogId(executeLogId)
                    .orElseThrow(() -> {
                        log.error("执行日志 {} 没有关联的录制记录", executeLogId);
                        return new RuntimeException("该执行日志没有关联的录制记录");
                    });

            log.info("找到关联记录: executeLogId={}, recordId={}", executeLogId, relation.getRecordId());

            // 2. 通过 recordId 查找录制记录
            Record record = recordRepository.findById(relation.getRecordId())
                    .orElseThrow(() -> {
                        log.error("录制记录 {} 不存在", relation.getRecordId());
                        return new RuntimeException("录制记录不存在");
                    });

            log.info("找到录制记录: recordId={}, storageType={}, recordUrl={}", 
                    record.getId(), record.getStorageType(), record.getRecordUrl());

            // 3. 检查是否有转换后的WebM文件，如果没有则按需转换
            String webmPath = null;
            Map<String, Object> metadata = parseMetadata(record.getMetadata());
            if (metadata != null && Boolean.TRUE.equals(metadata.get("has_webm_conversion"))) {
                webmPath = (String) metadata.get("converted_webm_path");
                if (webmPath != null && Files.exists(Paths.get(webmPath))) {
                    log.info("找到转换后的WebM文件: {}", webmPath);
                } else {
                    webmPath = null;
                }
            }
            
            // 如果没有WebM文件，尝试按需转换
            if (webmPath == null && "local".equalsIgnoreCase(record.getStorageType())) {
                String aviPath = record.getRecordUrl();
                if (aviPath != null && aviPath.toLowerCase().endsWith(".avi")) {
                    log.info("尝试按需转换AVI文件: {}", aviPath);
                    webmPath = videoConversionService.convertOnDemand(aviPath);
                    if (webmPath != null) {
                        log.info("按需转换成功: {}", webmPath);
                    } else {
                        log.warn("按需转换失败，将使用原始AVI文件");
                    }
                }
            }
            
            // 4. 根据存储类型获取视频数据
            byte[] videoData;
            String contentType = "video/x-msvideo"; // 默认为 AVI 格式
            
            if ("database".equalsIgnoreCase(record.getStorageType()) || "db".equalsIgnoreCase(record.getStorageType())) {
                // 从数据库获取
                videoData = record.getRecordData();
                if (videoData == null || videoData.length == 0) {
                    log.error("录制数据为空，recordId={}", record.getId());
                    throw new RuntimeException("录制数据为空");
                }
                log.info("从数据库加载录制视频，大小: {} bytes", videoData.length);
            } else if ("local".equalsIgnoreCase(record.getStorageType())) {
                // 从本地文件系统获取
                Path path;
                
                // 优先使用转换后的文件（WebM或MP4）
                if (webmPath != null) {
                    path = Paths.get(webmPath);
                    if (webmPath.toLowerCase().endsWith(".mp4")) {
                        contentType = "video/mp4";
                        log.info("使用转换后的MP4文件: {}", webmPath);
                    } else {
                        contentType = "video/webm";
                        log.info("使用转换后的WebM文件: {}", webmPath);
                    }
                } else {
                    // 使用原始文件
                    String filePath = record.getRecordUrl();
                    if (filePath == null || filePath.isEmpty()) {
                        log.error("录制文件路径为空，recordId={}", record.getId());
                        throw new RuntimeException("录制文件路径为空");
                    }
                    
                    log.info("尝试读取原始文件: {}", filePath);
                    path = Paths.get(filePath);
                    if (!Files.exists(path)) {
                        log.error("录制文件不存在: {}", filePath);
                        // 尝试新的recordings目录
                        Path recordingsPath = Paths.get("recordings", path.getFileName().toString());
                        if (Files.exists(recordingsPath)) {
                            log.info("找到recordings目录文件: {}", recordingsPath);
                            path = recordingsPath;
                        } else {
                            // 尝试旧的target/recordings目录（向后兼容）
                            Path oldPath = Paths.get("target/recordings", path.getFileName().toString());
                            if (Files.exists(oldPath)) {
                                log.info("找到旧的target/recordings目录文件: {}", oldPath);
                                path = oldPath;
                            } else {
                                throw new RuntimeException("录制文件不存在: " + filePath);
                            }
                        }
                    }
                }
                
                videoData = Files.readAllBytes(path);
                log.info("从本地文件加载录制视频: {}, 大小: {} bytes", path, videoData.length);
                
                // 根据文件扩展名设置正确的 Content-Type
                String fileName = path.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".mp4")) {
                    contentType = "video/mp4";
                } else if (fileName.endsWith(".webm")) {
                    contentType = "video/webm";
                } else if (fileName.endsWith(".avi")) {
                    contentType = "video/x-msvideo";
                }
            } else {
                log.error("不支持的存储类型: {}, recordId={}", record.getStorageType(), record.getId());
                throw new RuntimeException("不支持的存储类型: " + record.getStorageType());
            }

            // 4. 返回视频数据
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(videoData.length);
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            // 添加CORS头
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, OPTIONS");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization");
            
            return new ResponseEntity<>(videoData, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            log.error("读取录制文件失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("读取录制文件失败: " + e.getMessage()).getBytes());
        } catch (RuntimeException e) {
            log.error("获取录制视频失败", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((e.getMessage()).getBytes());
        }
    }

    /**
     * 按需转换视频API
     */
    @PostMapping("/convert/{executeLogId}")
    public ResponseEntity<Map<String, Object>> convertVideo(@PathVariable("executeLogId") Long executeLogId) {
        try {
            log.info("开始按需转换执行日志 {} 的录制视频", executeLogId);
            
            // 1. 通过 executeLogId 查找关联的 recordId
            ExecuteLogRecordRelated relation = executeLogRecordRelatedRepository
                    .findByExecuteLogId(executeLogId)
                    .orElseThrow(() -> {
                        log.error("执行日志 {} 没有关联的录制记录", executeLogId);
                        return new RuntimeException("该执行日志没有关联的录制记录");
                    });

            // 2. 通过 recordId 查找录制记录
            Record record = recordRepository.findById(relation.getRecordId())
                    .orElseThrow(() -> {
                        log.error("录制记录 {} 不存在", relation.getRecordId());
                        return new RuntimeException("录制记录不存在");
                    });

            // 3. 检查是否为本地存储的AVI文件
            if (!"local".equalsIgnoreCase(record.getStorageType())) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "只有本地存储的AVI文件才能转换"
                ));
            }

            String aviPath = record.getRecordUrl();
            if (aviPath == null || !aviPath.toLowerCase().endsWith(".avi")) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "不是AVI格式文件"
                ));
            }

            // 4. 执行按需转换
            String webmPath = videoConversionService.convertOnDemand(aviPath);
            
            if (webmPath != null) {
                // 更新元数据
                Map<String, Object> metadata = parseMetadata(record.getMetadata());
                if (metadata == null) {
                    metadata = new HashMap<>();
                }
                metadata.put("converted_webm_path", webmPath);
                metadata.put("has_webm_conversion", true);
                record.setMetadata(JSON.toJSONString(metadata));
                recordRepository.save(record);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "转换成功",
                    "webmPath", webmPath,
                    "originalPath", aviPath
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "转换失败，请检查FFmpeg是否安装"
                ));
            }

        } catch (Exception e) {
            log.error("按需转换失败", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "转换失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 解析元数据JSON字符串
     */
    private Map<String, Object> parseMetadata(String metadataJson) {
        try {
            if (metadataJson == null || metadataJson.trim().isEmpty()) {
                return new HashMap<>();
            }
            return JSON.parseObject(metadataJson, Map.class);
        } catch (Exception e) {
            log.warn("解析元数据失败: {}", e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * 从元数据中获取字符串值
     */
    private String getStringFromMetadata(Map<String, Object> metadata, String key, String defaultValue) {
        if (metadata == null || !metadata.containsKey(key)) {
            return defaultValue;
        }
        Object value = metadata.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * 根据格式和文件扩展名获取Content-Type
     */
    private String getContentTypeByFormat(String format, String fileExtension) {
        // 优先使用文件扩展名
        if (fileExtension != null && !fileExtension.isEmpty()) {
            switch (fileExtension.toLowerCase()) {
                case "mp4":
                    return "video/mp4";
                case "webm":
                    return "video/webm";
                case "avi":
                    return "video/x-msvideo";
                case "mov":
                    return "video/quicktime";
                default:
                    break;
            }
        }
        
        // 如果文件扩展名不可用，使用格式信息
        if (format != null) {
            switch (format.toLowerCase()) {
                case "mp4":
                    return "video/mp4";
                case "webm":
                    return "video/webm";
                case "avi":
                    return "video/x-msvideo";
                case "mov":
                    return "video/quicktime";
                default:
                    break;
            }
        }
        
        // 默认返回AVI格式
        return "video/x-msvideo";
    }
}

