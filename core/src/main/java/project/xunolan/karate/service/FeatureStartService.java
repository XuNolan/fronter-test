package project.xunolan.karate.service;

import com.intuit.karate.Runner;
import com.intuit.karate.Suite;
import com.intuit.karate.core.*;
import com.intuit.karate.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.database.entity.Script;
import project.xunolan.karate.adapter.ResourceAdapter;
import project.xunolan.karate.service.hooks.HookService;

import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FeatureStartService {
    public final static ThreadLocal<Session> currentlyUseSession = new ThreadLocal<>();

    @Autowired
    HookService hookService;

    public void RunScript(Session session, Script script){
        //基于script构造feature；
        String scriptData = script.getData();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(scriptData.getBytes(StandardCharsets.UTF_8));
        Resource resource = new ResourceAdapter(byteArrayInputStream);
        //主要就是用在getStream。
        Feature feature = Feature.read(resource);
        RunKarateFeature(feature, session);
    }

    private void RunKarateFeature(Feature feature, Session session){
        currentlyUseSession.set(session);
        FeatureCall featureCall = new FeatureCall(feature);
        Runner.Builder customizeBuilder = Runner.builder().hooks(hookService.getCommonHooks()).forTempUse();
        //设置forTempUse，为了使当前线程与实际FeatureRuntime执行线程在同一线程中，使得ThreadLocal有效。

        //这里可以存放多个featureCall。
        FeatureRuntime featureRuntime = FeatureRuntime.of(new Suite(customizeBuilder), featureCall, null);
        featureRuntime.run();
        currentlyUseSession.remove();
    }

}