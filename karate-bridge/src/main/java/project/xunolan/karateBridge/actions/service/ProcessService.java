package project.xunolan.karateBridge.actions.service;

import com.intuit.karate.Runner;
import com.intuit.karate.Suite;
import com.intuit.karate.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.karateBridge.actions.service.hooks.HookService;
import project.xunolan.karateBridge.infos.service.entity.FeatureInfo;


@Service
public class ProcessService {

    @Autowired
    HookService hookService;

    public void RunFeature(FeatureInfo featureInfo){
        /*
        Feature feature = Feature.read("classpath:com/intuit/karate/core/single-scenario.feature");
        FeatureCall featureCall = new FeatureCall(feature, "@Scenario2", -1, null);
        FeatureRuntime featureRuntime = FeatureRuntime.of(new Suite(), featureCall, null);
        featureRuntime.run();

        public Suite(Runner.Builder rb)
         */
        Feature feature = featureInfo.getFeature();
        FeatureCall featureCall = new FeatureCall(feature);
        Runner.Builder customizeBuilder = Runner.builder().hooks(hookService.getCommonHooks());

        FeatureRuntime featureRuntime = FeatureRuntime.of(new Suite(customizeBuilder), featureCall, null);
        featureRuntime.run();
        //处理assertEquals即可。
        FeatureResult featureResult = featureRuntime.result; //虽然好像没有啥用。
    }
}
