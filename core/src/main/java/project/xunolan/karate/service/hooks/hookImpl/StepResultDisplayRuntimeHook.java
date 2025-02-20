package project.xunolan.karate.service.hooks.hookImpl;

import com.alibaba.fastjson.JSON;
import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.*;
import lombok.extern.slf4j.Slf4j;
import project.xunolan.karate.adapter.ScenarioInfo;
import project.xunolan.karate.adapter.StepInfo;
import project.xunolan.karate.service.FeatureStartService;
import project.xunolan.websocket.entity.send.SendEntity;
import project.xunolan.websocket.entity.send.SendMsgType;
import project.xunolan.websocket.entity.send.impl.ExecuteResultInfo;
import project.xunolan.websocket.entity.send.impl.KarateFeatureInfo;
import project.xunolan.websocket.queue.SocketPackage;

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


}