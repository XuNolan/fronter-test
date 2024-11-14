package project.xunolan.karateBridge.actions.service.hooks.hookImpl;

import com.intuit.karate.RuntimeHook;
import com.intuit.karate.core.ScenarioRuntime;
import com.intuit.karate.core.Step;
import com.intuit.karate.core.StepResult;

public class StepControlRuntimeHook implements RuntimeHook {
    //这里需要完成的即为，
    //控制执行的暂停、终止。（未来）将实现断点调试和单步调试，以及时间速率执行交互。
    //执行线程加一个等待条件？
    //明天就去看execute的线程逻辑。中断也是要处理的。明早一来就看这个。
    private volatile boolean needBreak = false;

    @Override
    public boolean beforeStep(Step step, ScenarioRuntime sr) { //返回false控制停止。
        return true;
    }

    @Override
    public void afterStep(StepResult result, ScenarioRuntime sr) {

    }
}
