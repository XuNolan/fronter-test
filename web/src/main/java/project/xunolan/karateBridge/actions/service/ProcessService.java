package project.xunolan.karateBridge.actions.service;

import com.intuit.karate.Runner;
import com.intuit.karate.Suite;
import com.intuit.karate.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.karateBridge.actions.service.hooks.HookService;
import project.xunolan.karateBridge.infos.service.entity.FeatureInfo;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;


@Service
public class ProcessService {

    @Autowired
    HookService hookService;
    //上下文。因为实在找不到能传到runtime里面的自定义context方法了。
    public static Map<Object, Object> runtimeInfoCache = new HashMap<>();
    public static String sessionId_key = "SESSION_ID_KEY";
    public static String featureId_key= "FEATURE_ID_KEY";

    public void RunFeature(Session session, FeatureInfo featureInfo){
        runtimeInfoCache.clear();
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
        runtimeInfoCache.put(sessionId_key, session.getId());

        FeatureRuntime featureRuntime = FeatureRuntime.of(new Suite(customizeBuilder), featureCall, null);

        runtimeInfoCache.put(featureId_key, featureInfo.getFeatureId());

        featureRuntime.run();
        //处理assertEquals即可。
        FeatureResult featureResult = featureRuntime.result; //虽然好像没有啥用。
    }
}
