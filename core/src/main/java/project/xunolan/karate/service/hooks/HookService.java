package project.xunolan.karate.service.hooks;

import com.intuit.karate.RuntimeHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.karate.service.hooks.hookImpl.FeatureRecordRuntimeHook;
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

    public List<RuntimeHook> getRecordHooks() {
        List<RuntimeHook> hooks = new ArrayList<>();
        //先执行录制hook，再执行log记录。
        //关联关系在record hook中完成。都ok。log的hook在beforeFeature中已经预分配了预分配 execute_log 的待插入记录并存储到了session中。
        hooks.add(new FeatureRecordRuntimeHook());
        hooks.add(new StepResultDisplayRuntimeHook());
        return hooks;
    }
}