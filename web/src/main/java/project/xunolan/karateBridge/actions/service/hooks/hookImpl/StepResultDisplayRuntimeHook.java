package project.xunolan.karateBridge.actions.service.hooks.hookImpl;

import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.*;

import project.xunolan.karateBridge.actions.service.ProcessService;
import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.karateBridge.infos.service.entity.StepInfo;
import project.xunolan.web.entity.send.entity.impl.ExecuteResultInfo;
import project.xunolan.web.server.WebSocketServer;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StepResultDisplayRuntimeHook implements RuntimeHook {

    private static final Map<String, String> context = new HashMap<>();
    private static final String runtimeFeatureId = "RUMTIME_FEATURE_ID";
    private static final String runtimeScenarioId = "RUMTIME_SCENARIO_ID";
    private static final String runtimeStepId = "RUMTIME_STEP_ID";

    @Override
    public boolean beforeFeature(FeatureRuntime fr) {
        context.clear();
        context.put(runtimeFeatureId, (String) ProcessService.runtimeInfoCache.get(ProcessService.featureId_key));
        return true;
    }

    @Override
    public void afterFeature(FeatureRuntime fr) {
        context.clear();
    }

    @Override
    public boolean beforeScenario(ScenarioRuntime sr) { //定位scenarioId；
//        int i = 0;
//        for (Iterator<ScenarioRuntime> it = sr.featureRuntime.scenarios; it.hasNext(); ) {
//            ScenarioRuntime scenarioRuntime = it.next();
//            if(scenarioRuntime.equals(sr)){
//                context.put(runtimeScenarioId, "" + i);
//                break;
//            }
//            i++;
//        }
        //暂时用senarioResult的大小来判断。如果遇到跳过的scenario就没办法了。
        //todo：之后考虑用更好一点的id处理和定位step方法。
        context.put(runtimeScenarioId, "" + sr.featureRuntime.result.getScenarioResults().size());
        assert context.containsKey(runtimeScenarioId);
        return true;
    }

    @Override
    public void afterScenario(ScenarioRuntime sr) {
        context.remove(runtimeScenarioId);
    }

    @Override
    public boolean beforeStep(Step step, ScenarioRuntime sr) {
        for(int i =0; i < sr.scenario.getSteps().size(); i++){
            if(sr.scenario.getSteps().get(i).equals(step)){
                context.put(runtimeStepId, ""+i);
                break;
            }
        }
        assert context.containsKey(runtimeStepId);
        return true;
    }


    @Override
    public void afterStep(StepResult stepResult, ScenarioRuntime sr) {
        Result result = stepResult.getResult();
        //所以这里必须要用到web模块了。这里必须解决循环依赖问题。
        //主要是需要传出单步执行的结果信息。一旦在这里完成构造并发送，就必须引用web模块。
        ExecuteResultInfo executeResultInfo = ExecuteResultInfo.fromResult(result);
        executeResultInfo.setFeatureId(context.get(runtimeFeatureId));
        executeResultInfo.setScenarioId(context.get(runtimeScenarioId));
        executeResultInfo.setStepId(context.get(runtimeStepId));
        String sessionId = (String) ProcessService.runtimeInfoCache.get(ProcessService.sessionId_key);
        executeResultInfo.constructAndSendExecuteResultInfo(WebSocketServer.sessionMap.get(sessionId));

        context.remove(runtimeStepId);
    }

}
