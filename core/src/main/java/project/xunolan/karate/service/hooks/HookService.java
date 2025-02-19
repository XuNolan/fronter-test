package project.xunolan.karate.service.hooks;

import com.intuit.karate.RuntimeHook;
import org.springframework.stereotype.Service;
import project.xunolan.karate.service.hooks.hookImpl.StepResultDisplayRuntimeHook;

import java.util.ArrayList;
import java.util.List;

@Service
public class HookService {

    public List<RuntimeHook> getCommonHooks() {
        List<RuntimeHook> hooks = new ArrayList<>();
        hooks.add(new StepResultDisplayRuntimeHook());
        //可以添加其他hooks；
        return hooks;
    }
}