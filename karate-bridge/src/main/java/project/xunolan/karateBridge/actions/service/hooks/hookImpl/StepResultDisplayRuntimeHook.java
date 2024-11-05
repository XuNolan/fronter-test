package project.xunolan.karateBridge.actions.service.hooks.hookImpl;

import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.*;
import project.xunolan.karateBridge.infos.entity.SendMessageType;
import project.xunolan.karateBridge.infos.entity.impl.ExecuteResultInfo;
import project.xunolan.karateBridge.infos.entity.impl.FeatureInfo;
import project.xunolan.karateBridge.infos.entity.impl.StepInfo;
import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.karateBridge.infos.utils.BeanUtils;

public class StepResultDisplayRuntimeHook implements RuntimeHook {


    @Override
    public void afterStep(StepResult stepResult, ScenarioRuntime sr) {
        Result result = stepResult.getResult();
        Step step = stepResult.getStep();
        StepInfo stepInfo = FeatureService.getStepInfoMap().get(step);
        ExecuteResultInfo executeResultInfo = ExecuteResultInfo.fromResult(result);
        executeResultInfo.constructExecuuteResultInfo();

    }
}
