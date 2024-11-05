package project.xunolan.karateBridge.actions.service.hooks;

import com.intuit.karate.RuntimeHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import project.xunolan.karateBridge.actions.service.hooks.hookImpl.StepControlRuntimeHook;

import java.util.ArrayList;
import java.util.List;

@Service
public class HookService {

    public List<RuntimeHook> getCommonHooks() {
        List<RuntimeHook> hooks = new ArrayList<>();
        hooks.add(new StepControlRuntimeHook());
        return hooks;
    }
}
