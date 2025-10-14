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
        ExecuteResultInfo executeResultInfo = ExecuteResultInfo.fromResult(nowProcessScenarioIndex, nowProcessStepIndex,stepResult);
        Session session = FeatureStartService.currentlyUseSession.get();
        SocketPackage.sendToExecuteLogQueue(new SocketPackage(session, JSON.toJSONString(new SendEntity(SendMsgType.ExecuteInfoMsg.getMsgType(), JSON.toJSONString(executeResultInfo)))));
        // 追加到 session 缓存的日志文本中（JSON 数组）
        String existed = SessionKeyEnum.ACC_EXECUTE_LOG.get(session);
        String append = JSON.toJSONString(executeResultInfo);
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
            return;
        }
        Long executeLogId = SessionKeyEnum.EXECUTE_LOG_ID.get(session);
        Long executeStartTime = SessionKeyEnum.EXECUTE_START_TIME.get(session);
        Integer status = SessionKeyEnum.FEATURE_STATUS.getOrDefault(session, 0);

        
        String existed = SessionKeyEnum.ACC_EXECUTE_LOG.get(session);
        String finalLog = (existed == null ? "[]" : existed + "]");

        try {
            ExecuteLogRepository executeLogRepository = BeanUtils.getBean(ExecuteLogRepository.class);
            ExecuteLog row = executeLogRepository.findById(executeLogId).orElse(null);
            if (row != null) {
                row.setLogData(finalLog);
                row.setStatus(status);
                int elapsed = executeStartTime == null ? 0 : (int)(System.currentTimeMillis() - executeStartTime);
                row.setExecuteTime(elapsed);
                executeLogRepository.save(row);
            }
        } catch (Exception e) {
            log.error("save execute_log failed", e);
        } finally {
            SessionKeyEnum.ACC_EXECUTE_LOG.set(session, null);
        }
    }
}
//执行完毕为0，执行终止或日志保存失败为2.