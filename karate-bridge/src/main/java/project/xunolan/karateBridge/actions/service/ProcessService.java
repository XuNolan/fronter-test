package project.xunolan.karateBridge.actions.service;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.Suite;
import com.intuit.karate.core.Feature;
import org.springframework.stereotype.Service;

@Service
public class ProcessService {

    public void RunFeature(Feature feature){
        //FeatureRuntime fr = FeatureRuntime.forTempUse(); 无配置方式；
//        FeatureRuntime fr = FeatureRuntime.of(new Suite(builder), new FeatureCall(feature));
//        fr.run();

        StepControlRuntimeHook stepControlRuntimeHook = new StepControlRuntimeHook();
        Results results = Runner.builder().hook(stepControlRuntimeHook).parallel(1);
        //处理assertEquals即可。
    }

    private Suite getCustomiseSuite(){

    }
}
