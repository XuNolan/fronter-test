package project.xunolan.karate.service.hooks.hookImpl;

import com.alibaba.fastjson.JSON;
import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import project.xunolan.database.entity.ExecuteLog;
import project.xunolan.karate.adapter.ScenarioInfo;
import project.xunolan.karate.adapter.StepInfo;
import project.xunolan.karate.service.FeatureStartService;
import project.xunolan.database.repository.ExecuteLogRepository;
import project.xunolan.websocket.entity.send.SendEntity;
import project.xunolan.websocket.entity.send.SendMsgType;
import project.xunolan.websocket.entity.send.impl.ExecuteResultInfo;
import project.xunolan.websocket.entity.send.impl.KarateFeatureInfo;
import project.xunolan.websocket.queue.SocketPackage;
import project.xunolan.websocket.utils.BeanUtils;
import project.xunolan.service.SessionKeyEnum;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class StepResultDisplayRuntimeHook implements RuntimeHook {
    private int nowProcessScenarioIndex;
    private int nowProcessStepIndex;

    public boolean beforeFeature(FeatureRuntime fr) {
        Session session = FeatureStartService.currentlyUseSession.get();
        if (session == null) {
            log.error("session is null");
            return false;
        }

        // 预分配 execute_log 的待插入记录（生成空日志占位）
        Long scriptId = SessionKeyEnum.SCRIPT_ID.get(session);
        Long usecaseId = SessionKeyEnum.USECASE_ID.get(session);
        Long executeGroupId = SessionKeyEnum.EXECUTE_GROUP_ID.getOrDefault(session, 0L);
        ExecuteLogRepository executeLogRepository = BeanUtils.getBean(ExecuteLogRepository.class);
        ExecuteLog logRow = ExecuteLog.builder()
                .scriptId(scriptId)
                .usecaseId(usecaseId)
                .executeGroupId(executeGroupId)
                .logData("")
                .executeTime(0)
                .status(2) // 2 执行中止/进行中
                .created((int)(System.currentTimeMillis() / 1000))
                .build();
        logRow = executeLogRepository.save(logRow);
        SessionKeyEnum.EXECUTE_LOG_ID.set(session, logRow.getId());
        SessionKeyEnum.EXECUTE_START_TIME.set(session, System.currentTimeMillis());

        KarateFeatureInfo karateStepInfo = constructKarateFeatureInfo(fr);
        SocketPackage.sendToScenarioInfoQueue(new SocketPackage(session, JSON.toJSONString(new SendEntity(SendMsgType.KarateFeatureInfoMsg.getMsgType(), JSON.toJSONString(karateStepInfo)))));
        nowProcessScenarioIndex = -1;
        return true;
    }

    @Override
    public boolean beforeScenario(ScenarioRuntime sr) {
        nowProcessScenarioIndex ++;
        nowProcessStepIndex = -1;
        return true;
    }

    @Override
    public boolean beforeStep(Step step, ScenarioRuntime sr) {
        nowProcessStepIndex++;
        return true;
    }

    @Override
    public void afterStep(StepResult stepResult, ScenarioRuntime sr) {
        ExecuteResultInfo executeResultInfo = ExecuteResultInfo.fromResult(nowProcessScenarioIndex, nowProcessStepIndex, stepResult);
        Session session = FeatureStartService.currentlyUseSession.get();
        // 向前端实时推送（保持不变）
        SocketPackage.sendToExecuteLogQueue(new SocketPackage(session, JSON.toJSONString(new SendEntity(SendMsgType.ExecuteInfoMsg.getMsgType(), JSON.toJSONString(executeResultInfo)))));

        // 构造更丰富的 step 日志条目：包含原始脚本、索引、以及 fromResult 序列化后的完整明细
        String rawStep = stepResult.getStep().getPrefix() + " " + stepResult.getStep().getText();
        java.util.Map<String, Object> stepLogEntry = new java.util.LinkedHashMap<>();
        stepLogEntry.put("scenarioIndex", nowProcessScenarioIndex);
        stepLogEntry.put("stepIndex", nowProcessStepIndex);
        stepLogEntry.put("stepString", rawStep);
        // 详细执行信息（包含开始/结束时间、耗时、是否成功、错误信息、可能的日志等，由 fromResult 提供）
        stepLogEntry.put("detail", executeResultInfo);

        // 追加到 session 缓存（JSON 数组字符串）
        String existed = SessionKeyEnum.ACC_EXECUTE_LOG.get(session);
        String append = JSON.toJSONString(stepLogEntry);
        log.info("afterStep - scenarioIndex: {}, stepIndex: {}, existed: '{}', append: '{}'", 
                nowProcessScenarioIndex, nowProcessStepIndex, existed, append);
        if (!StringUtils.hasText(existed)) {
            SessionKeyEnum.ACC_EXECUTE_LOG.set(session, "[" + append);
        } else {
            SessionKeyEnum.ACC_EXECUTE_LOG.set(session, existed + "," + append);
        }
    }


    private KarateFeatureInfo constructKarateFeatureInfo(FeatureRuntime fr) {
        List<ScenarioInfo> scenarios = new ArrayList<>();
        Iterator<ScenarioRuntime> scenarioRuntimeIterator = new ScenarioIterator(fr).filterSelected().iterator(); //与FreatureRuntime构造函数中的Iterator<ScenarioRuntime>初始化一致。因为其final，用后无法重置，所以自行构造
        int scenarioNum = 0;
        while(scenarioRuntimeIterator.hasNext()){
            ScenarioRuntime scenarioRuntime = scenarioRuntimeIterator.next();
            List<StepInfo> stepInfos = new ArrayList<>();
            scenarioRuntime.scenario.getSteps().forEach(step -> stepInfos.add(new StepInfo(step.getIndex(), step.getPrefix() + " " + step.getText())));
            ScenarioInfo scenarioInfo = ScenarioInfo.builder()
                    .index(scenarioNum)
                    .scenarioName(scenarioRuntime.scenario.getName())
                    .stepInfos(stepInfos)
                    .build();
            scenarios.add(scenarioInfo);
            scenarioNum++;
        }
        return new KarateFeatureInfo(scenarios);
    }

    @Override
    public void afterFeature(FeatureRuntime fr) {
        //完成统计信息的实际数据库存储。
        Session session = FeatureStartService.currentlyUseSession.get();
        if (session == null) {
            log.warn("session is null in afterFeature");
            return;
        }
        Long executeLogId = SessionKeyEnum.EXECUTE_LOG_ID.get(session);
        Long executeStartTime = SessionKeyEnum.EXECUTE_START_TIME.get(session);
        Integer status = SessionKeyEnum.FEATURE_STATUS.getOrDefault(session, 0);

        
        String existed = SessionKeyEnum.ACC_EXECUTE_LOG.get(session);
        String finalLog = (existed == null ? "[]" : existed + "]");
        log.info("afterFeature - executeLogId: {}, existed: '{}', finalLog: '{}', status: {}", executeLogId, existed, finalLog, status);

        try {
            ExecuteLogRepository executeLogRepository = BeanUtils.getBean(ExecuteLogRepository.class);
            ExecuteLog row = executeLogRepository.findById(executeLogId).orElse(null);
            if (row != null) {
                row.setLogData(finalLog);
                row.setStatus(status);
                int elapsed = executeStartTime == null ? 0 : (int)(System.currentTimeMillis() - executeStartTime);
                row.setExecuteTime(elapsed);
                executeLogRepository.save(row);
                log.info("saved execute_log with logData: '{}', status: {}", finalLog, status);
            } else {
                log.warn("execute_log not found for id: {}", executeLogId);
            }
        } catch (Exception e) {
            log.error("save execute_log failed", e);
        } finally {
            SessionKeyEnum.ACC_EXECUTE_LOG.remove(session);
        }
    }
}
//执行完毕为0，执行终止或日志保存失败为2.