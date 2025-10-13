package project.xunolan.web.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.xunolan.common.entity.BasicResultVO;
import project.xunolan.common.enums.RespStatusEnum;
import project.xunolan.database.entity.ExecuteGroupId;
import project.xunolan.database.entity.ExecuteGroupScriptRelated;
import project.xunolan.database.entity.Script;
import project.xunolan.database.repository.ExecuteGroupIdRepository;
import project.xunolan.database.repository.ExecuteGroupScriptRelatedRepository;
import project.xunolan.service.ScriptService;
import project.xunolan.web.amisEntity.aspect.AmisResult;
import project.xunolan.web.amisEntity.entity.AmisCrudListVo;
import project.xunolan.web.amisEntity.utils.Convert4Amis;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 执行组管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/execute-group")
@AmisResult
public class ExecuteGroupController {

    @Autowired
    private ExecuteGroupIdRepository executeGroupIdRepository;

    @Autowired
    private ExecuteGroupScriptRelatedRepository executeGroupScriptRelatedRepository;

    @Autowired
    private ScriptService scriptService;

    @Autowired
    private project.xunolan.service.UsecaseService usecaseService;

    @Autowired
    private project.xunolan.database.repository.ScriptRepository scriptRepository;

    /**
     * 获取所有可用脚本（用于执行组选择）
     * 只返回启用的脚本，并包含用例名称
     * GET /execute-group/available-scripts
     */
    @GetMapping("/available-scripts")
    public BasicResultVO<List<Map<String, Object>>> getAvailableScripts() {
        try {
            // 获取所有启用的脚本
            List<Script> allScripts = scriptRepository.findAll();
            
            List<Map<String, Object>> scriptOptions = new ArrayList<>();
            for (Script script : allScripts) {
                // 只添加启用的脚本
                if (script.getIsActive() != null && script.getIsActive()) {
                    // 获取用例信息
                    project.xunolan.database.entity.Usecase usecase = usecaseService.queryById(script.getUsecaseId());
                    
                    Map<String, Object> option = new HashMap<>();
                    // 构建友好的标签：用例名 - 脚本名 (v版本)
                    String label = String.format("[%s] %s (v%s)", 
                        usecase != null ? usecase.getName() : "未知用例",
                        script.getName(),
                        script.getVersion());
                    
                    option.put("label", label);
                    option.put("value", script.getId());
                    option.put("scriptId", script.getId());
                    option.put("scriptName", script.getName());
                    option.put("version", script.getVersion());
                    option.put("description", script.getDescription());
                    option.put("usecaseId", script.getUsecaseId());
                    option.put("usecaseName", usecase != null ? usecase.getName() : "");
                    option.put("isActive", script.getIsActive());
                    
                    scriptOptions.add(option);
                }
            }
            
            // 按用例名排序
            scriptOptions.sort((a, b) -> {
                String nameA = (String) a.getOrDefault("usecaseName", "");
                String nameB = (String) b.getOrDefault("usecaseName", "");
                return nameA.compareTo(nameB);
            });
            
            return new BasicResultVO<>(RespStatusEnum.SUCCESS, null, scriptOptions);
        } catch (Exception e) {
            log.error("Failed to get available scripts", e);
            return new BasicResultVO<>(RespStatusEnum.FAIL, "获取脚本列表失败: " + e.getMessage(), null);
        }
    }

    /**
     * 获取执行组列表
     * GET /execute-group/list
     */
    @GetMapping("/list")
    public AmisCrudListVo getExecuteGroupList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "perPage", defaultValue = "10") int perPage,
            @RequestParam(value = "keywords", required = false) String keywords) {

        List<ExecuteGroupId> allGroups = executeGroupIdRepository.findAll();

        // 关键词过滤
        if (keywords != null && !keywords.isEmpty()) {
            allGroups = allGroups.stream()
                    .filter(group -> group.getGroupName().contains(keywords))
                    .collect(Collectors.toList());
        }

        // 构建返回数据
        List<Map<String, Object>> items = new ArrayList<>();
        for (ExecuteGroupId group : allGroups) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", group.getId());
            item.put("groupName", group.getGroupName());
            item.put("created", group.getCreated());
            item.put("updated", group.getUpdated());

            // 统计脚本数量
            long scriptCount = executeGroupScriptRelatedRepository.countByExecuteGroupId(group.getId());
            item.put("scriptCount", scriptCount);

            items.add(item);
        }

        // 简单分页（实际应用中可使用 Spring Data 分页）
        int total = items.size();
        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, total);
        List<Map<String, Object>> pagedItems = items.subList(Math.max(0, start), end);

        return AmisCrudListVo.builder()
                .total(total)
                .items(pagedItems)
                .page(page)
                .build();
    }

    /**
     * 创建执行组
     * POST /execute-group/create
     */
    @Transactional
    @PostMapping("/create")
    public BasicResultVO<Long> createExecuteGroup(@RequestBody CreateGroupRequest request) {
        try {
            // 检查组名是否已存在
            if (executeGroupIdRepository.existsByGroupName(request.getGroupName())) {
                return new BasicResultVO<>(RespStatusEnum.FAIL, "组名已存在", null);
            }

            int now = (int) (System.currentTimeMillis() / 1000);
            ExecuteGroupId group = ExecuteGroupId.builder()
                    .groupName(request.getGroupName())
                    .created(now)
                    .updated(now)
                    .build();

            group = executeGroupIdRepository.save(group);

            // 如果提供了脚本列表，创建关联关系
            if (request.getScripts() != null && !request.getScripts().isEmpty()) {
                List<ExecuteGroupScriptRelated> relations = new ArrayList<>();
                for (int i = 0; i < request.getScripts().size(); i++) {
                    ScriptItem scriptItem = request.getScripts().get(i);
                    ExecuteGroupScriptRelated relation = ExecuteGroupScriptRelated.builder()
                            .executeGroupId(group.getId())
                            .scriptId(scriptItem.getScriptId())
                            .index((long) i)
                            .build();
                    relations.add(relation);
                }
                // 批量保存关联关系
                executeGroupScriptRelatedRepository.saveAll(relations);
            }

            return new BasicResultVO<>(RespStatusEnum.SUCCESS, "创建成功", group.getId());
        } catch (Exception e) {
            log.error("Failed to create execute group", e);
            return new BasicResultVO<>(RespStatusEnum.FAIL, "创建失败: " + e.getMessage(), null);
        }
    }

    /**
     * 获取执行组详情（包含关联的脚本列表，带用例信息）
     * GET /execute-group/detail/{id}
     */
    @GetMapping("/detail/{id}")
    public BasicResultVO<ExecuteGroupDetailVO> getExecuteGroupDetail(@PathVariable Long id) {
        try {
            ExecuteGroupId group = executeGroupIdRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("执行组不存在"));

            List<ExecuteGroupScriptRelated> relations = executeGroupScriptRelatedRepository
                    .findByExecuteGroupIdOrderByIndexAsc(id);

            List<ScriptDetailVO> scripts = new ArrayList<>();
            for (ExecuteGroupScriptRelated relation : relations) {
                Script script = scriptService.queryScriptByScriptId(relation.getScriptId());
                if (script != null) {
                    // 获取用例信息
                    project.xunolan.database.entity.Usecase usecase = usecaseService.queryById(script.getUsecaseId());
                    
                    scripts.add(ScriptDetailVO.builder()
                            .index(relation.getIndex())
                            .scriptId(script.getId())
                            .scriptName(script.getName())
                            .scriptDescription(script.getDescription())
                            .version(script.getVersion())
                            .usecaseId(script.getUsecaseId())
                            .usecaseName(usecase != null ? usecase.getName() : "")
                            .usecaseDescription(usecase != null ? usecase.getDescription() : "")
                            .isActive(script.getIsActive())
                            .build());
                }
            }

            ExecuteGroupDetailVO detail = ExecuteGroupDetailVO.builder()
                    .id(group.getId())
                    .groupName(group.getGroupName())
                    .created(group.getCreated())
                    .updated(group.getUpdated())
                    .scripts(scripts)
                    .build();

            return new BasicResultVO<>(RespStatusEnum.SUCCESS, null, detail);
        } catch (Exception e) {
            log.error("Failed to get execute group detail", e);
            return new BasicResultVO<>(RespStatusEnum.FAIL, "查询失败: " + e.getMessage(), null);
        }
    }

    /**
     * 更新执行组（包含脚本列表）
     * POST /execute-group/update/{id}
     */
    @Transactional
    @PostMapping("/update/{id}")
    public BasicResultVO<Void> updateExecuteGroup(
            @PathVariable Long id,
            @RequestBody UpdateGroupRequest request) {
        try {
            ExecuteGroupId group = executeGroupIdRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("执行组不存在"));

            // 更新基本信息
            if (request.getGroupName() != null && !request.getGroupName().equals(group.getGroupName())) {
                if (executeGroupIdRepository.existsByGroupName(request.getGroupName())) {
                    return new BasicResultVO<>(RespStatusEnum.FAIL, "组名已存在", null);
                }
                group.setGroupName(request.getGroupName());
            }

            group.setUpdated((int) (System.currentTimeMillis() / 1000));
            executeGroupIdRepository.save(group);

            // 更新脚本关联（支持两种格式：scriptIds 或 scripts）
            if (request.getScripts() != null && !request.getScripts().isEmpty()) {
                // 先查询现有的关联记录
                List<ExecuteGroupScriptRelated> existingRelations = executeGroupScriptRelatedRepository
                        .findByExecuteGroupIdOrderByIndexAsc(id);
                
                // 删除所有现有关联
                if (!existingRelations.isEmpty()) {
                    executeGroupScriptRelatedRepository.deleteAll(existingRelations);
                    // 强制刷新，确保删除操作立即生效
                    executeGroupScriptRelatedRepository.flush();
                }

                // 创建新的关联（使用 scripts 格式）
                List<ExecuteGroupScriptRelated> newRelations = new ArrayList<>();
                for (int i = 0; i < request.getScripts().size(); i++) {
                    ScriptItem scriptItem = request.getScripts().get(i);
                    ExecuteGroupScriptRelated relation = ExecuteGroupScriptRelated.builder()
                            .executeGroupId(id)
                            .scriptId(scriptItem.getScriptId())
                            .index((long) i)
                            .build();
                    newRelations.add(relation);
                }
                // 批量保存新关联
                executeGroupScriptRelatedRepository.saveAll(newRelations);
                
            } else if (request.getScriptIds() != null && !request.getScriptIds().isEmpty()) {
                // 先查询现有的关联记录
                List<ExecuteGroupScriptRelated> existingRelations = executeGroupScriptRelatedRepository
                        .findByExecuteGroupIdOrderByIndexAsc(id);
                
                // 删除所有现有关联
                if (!existingRelations.isEmpty()) {
                    executeGroupScriptRelatedRepository.deleteAll(existingRelations);
                    // 强制刷新，确保删除操作立即生效
                    executeGroupScriptRelatedRepository.flush();
                }

                // 创建新的关联（使用 scriptIds 格式）
                List<ExecuteGroupScriptRelated> newRelations = new ArrayList<>();
                for (int i = 0; i < request.getScriptIds().size(); i++) {
                    ExecuteGroupScriptRelated relation = ExecuteGroupScriptRelated.builder()
                            .executeGroupId(id)
                            .scriptId(request.getScriptIds().get(i))
                            .index((long) i)
                            .build();
                    newRelations.add(relation);
                }
                // 批量保存新关联
                executeGroupScriptRelatedRepository.saveAll(newRelations);
            }

            return new BasicResultVO<>(RespStatusEnum.SUCCESS, "更新成功", null);
        } catch (Exception e) {
            log.error("Failed to update execute group", e);
            return new BasicResultVO<>(RespStatusEnum.FAIL, "更新失败: " + e.getMessage(), null);
        }
    }

    /**
     * 删除执行组
     * DELETE /execute-group/delete/{id}
     */
    @Transactional
    @DeleteMapping("/delete/{id}")
    public BasicResultVO<Void> deleteExecuteGroup(@PathVariable Long id) {
        try {
            // 删除关联关系
            executeGroupScriptRelatedRepository.deleteByExecuteGroupId(id);

            // 删除执行组
            executeGroupIdRepository.deleteById(id);

            return new BasicResultVO<>(RespStatusEnum.SUCCESS, "删除成功", null);
        } catch (Exception e) {
            log.error("Failed to delete execute group", e);
            return new BasicResultVO<>(RespStatusEnum.FAIL, "删除失败: " + e.getMessage(), null);
        }
    }

    /**
     * 执行组任务（顺序执行所有脚本）
     * POST /execute-group/execute/{id}
     */
    @PostMapping("/execute/{id}")
    public BasicResultVO<Void> executeGroup(@PathVariable Long id) {
        try {
            ExecuteGroupId group = executeGroupIdRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("执行组不存在"));

            List<ExecuteGroupScriptRelated> relations = executeGroupScriptRelatedRepository
                    .findByExecuteGroupIdOrderByIndexAsc(id);

            if (relations.isEmpty()) {
                return new BasicResultVO<>(RespStatusEnum.FAIL, "执行组中没有脚本", null);
            }

            // TODO: 实现顺序执行脚本的逻辑
            // 这里需要调用脚本执行服务，顺序执行每个脚本
            // 可以考虑异步执行，并记录执行日志到 execute_log 表

            log.info("Starting execution of group: {} with {} scripts", group.getGroupName(), relations.size());

            return new BasicResultVO<>(RespStatusEnum.SUCCESS, "执行任务已启动", null);
        } catch (Exception e) {
            log.error("Failed to execute group", e);
            return new BasicResultVO<>(RespStatusEnum.FAIL, "执行失败: " + e.getMessage(), null);
        }
    }

    // ===== DTO 类 =====

    @Data
    public static class CreateGroupRequest {
        private String groupName;
        private List<ScriptItem> scripts;  // 脚本列表（按顺序）
    }

    @Data
    public static class ScriptItem {
        private Long scriptId;
        private String scriptName;  // 可选，用于前端展示
        private String version;     // 可选，用于前端展示
    }

    @Data
    public static class UpdateGroupRequest {
        private String groupName;
        private List<Long> scriptIds;  // 兼容旧格式
        private List<ScriptItem> scripts;  // 新格式，支持拖拽排序
    }

    @Data
    @lombok.Builder
    public static class ExecuteGroupDetailVO {
        private Long id;
        private String groupName;
        private Integer created;
        private Integer updated;
        private List<ScriptDetailVO> scripts;
    }

    @Data
    @lombok.Builder
    public static class ScriptDetailVO {
        private Long index;
        private Long scriptId;
        private String scriptName;
        private String scriptDescription;
        private String version;
        private Long usecaseId;
        private String usecaseName;
        private String usecaseDescription;
        private Boolean isActive;
    }

    @Data
    @lombok.Builder
    public static class ScriptInfoVO {
        private Long scriptId;
        private String scriptName;
        private String scriptDescription;
        private String version;
        private Long index;
    }
}

