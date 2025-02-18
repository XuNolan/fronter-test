package project.xunolan.websocket.karateBridge.service.hooks;

import com.intuit.karate.RuntimeHook;
import org.springframework.stereotype.Service;
import project.xunolan.websocket.karateBridge.service.hooks.hookImpl.StepResultDisplayRuntimeHook;

import java.util.ArrayList;
import java.util.List;

@Service
public class HookService {

    public List<RuntimeHook> getCommonHooks() {
        List<RuntimeHook> hooks = new ArrayList<>();
        hooks.add(new StepResultDisplayRuntimeHook());
        return hooks;
    }
}