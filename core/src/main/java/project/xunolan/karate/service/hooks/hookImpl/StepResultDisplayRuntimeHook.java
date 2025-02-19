package project.xunolan.karate.service.hooks.hookImpl;

import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.*;
import lombok.extern.slf4j.Slf4j;
import project.xunolan.karate.service.FeatureStartService;
import project.xunolan.websocket.entity.send.impl.ExecuteResultInfo;
import project.xunolan.websocket.queue.SocketPackage;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StepResultDisplayRuntimeHook implements RuntimeHook {

    public boolean beforeFeature(FeatureRuntime fr) {
        Session session = FeatureStartService.currentlyUseSession.get();
        if (session == null) {
            log.error("session is null");
            return false;
        }
        //todo: 将要执行的step信息和编号传回给前端。
        return true;
    }


    @Override
    public boolean beforeStep(Step step, ScenarioRuntime sr) {
        return true;
    }


    @Override
    public void afterStep(StepResult stepResult, ScenarioRuntime sr) {
        ExecuteResultInfo executeResultInfo = ExecuteResultInfo.fromResult(stepResult, sr);
        Session session = FeatureStartService.currentlyUseSession.get();
        SocketPackage.sendToExecuteLogQueue(new SocketPackage(session, executeResultInfo));
    }

}