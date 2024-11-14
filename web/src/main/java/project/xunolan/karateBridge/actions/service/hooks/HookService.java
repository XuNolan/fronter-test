package project.xunolan.karateBridge.actions.service.hooks;

import com.intuit.karate.RuntimeHook;
import org.springframework.stereotype.Service;
import project.xunolan.karateBridge.actions.service.hooks.hookImpl.StepControlRuntimeHook;
import project.xunolan.karateBridge.actions.service.hooks.hookImpl.StepLogCollectRuntimeHook;
import project.xunolan.karateBridge.actions.service.hooks.hookImpl.StepResultDisplayRuntimeHook;

import java.util.ArrayList;
import java.util.List;

@Service
public class HookService {

    public List<RuntimeHook> getCommonHooks() {
        List<RuntimeHook> hooks = new ArrayList<>();
        hooks.add(new StepControlRuntimeHook());
        hooks.add(new StepResultDisplayRuntimeHook());
        hooks.add(new StepLogCollectRuntimeHook());
        return hooks;
    }
}
