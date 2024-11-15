package project.xunolan.karateBridge.actions.service.hooks.hookImpl;

import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.*;

import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.karateBridge.infos.service.entity.StepInfo;
import project.xunolan.web.entity.send.entity.impl.ExecuteResultInfo;

public class StepResultDisplayRuntimeHook implements RuntimeHook {

    @Override
    public void afterStep(StepResult stepResult, ScenarioRuntime sr) {
        Result result = stepResult.getResult();
        Step step = stepResult.getStep();
        StepInfo stepInfo = FeatureService.getStepInfoMap().get(step);
        String stepId = stepInfo.getStepId();
        String featureId = stepId.substring(0, stepId.indexOf(FeatureService.getID_INTERVAL()));
        String scenarioId = stepId.substring(stepId.indexOf(FeatureService.getID_INTERVAL()) + FeatureService.getID_INTERVAL().length()
                        , stepId.lastIndexOf(FeatureService.getID_INTERVAL()));
        //所以这里必须要用到web模块了。这里必须解决循环依赖问题。
        //主要是需要传出单步执行的结果信息。一旦在这里完成构造并发送，就必须引用web模块。
        ExecuteResultInfo executeResultInfo = ExecuteResultInfo.fromResult(result);
        executeResultInfo.setFeatureId(featureId);
        executeResultInfo.setScenarioId(scenarioId);
        executeResultInfo.setStepId(stepId);
        executeResultInfo.constructAndSendExecuteResultInfo();
    }

}
