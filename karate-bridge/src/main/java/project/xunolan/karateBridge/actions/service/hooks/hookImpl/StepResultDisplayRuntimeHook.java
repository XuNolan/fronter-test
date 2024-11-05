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
        //所以这里必须要用到web模块了。
        ExecuteResultInfo executeResultInfo = ExecuteResultInfo.fromResult(result);
        executeResultInfo.constructAndSendExecuteResultInfo();
    }

}
