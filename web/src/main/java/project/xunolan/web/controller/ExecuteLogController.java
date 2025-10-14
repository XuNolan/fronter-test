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
import project.xunolan.database.entity.Script;
import project.xunolan.web.amisEntity.entity.AmisCrudListVo;
import project.xunolan.web.amisEntity.utils.Convert4Amis;

import java.util.*;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import java.nio.file.*;

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

        Map<String, Object> result = Convert4Amis.flatSingleMapWithPrefix("", executeLog);
        result.put("statusText", getStatusText(executeLog.getStatus()));

        // 查找关联的录制记录
        ExecuteLogRecordRelated relation = executeLogRecordRelatedRepository
                .findByExecuteLogId(id).orElse(null);
        
        if (relation != null) {
            Record record = recordRepository.findById(relation.getRecordId()).orElse(null);
            if (record != null) {
                Map<String, Object> recordData = Convert4Amis.flatSingleMapWithPrefix("record_", record);
                result.putAll(recordData);
                result.put("hasRecord", true);
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
}

