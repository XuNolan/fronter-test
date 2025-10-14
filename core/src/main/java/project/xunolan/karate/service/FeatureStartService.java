package project.xunolan.karate.service;

import com.intuit.karate.Runner;
import com.intuit.karate.Suite;
import com.intuit.karate.core.*;
import com.intuit.karate.RuntimeHook;
import com.intuit.karate.resource.MemoryResource;
import com.intuit.karate.resource.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.database.entity.Script;
import project.xunolan.karate.adapter.ResourceAdapter;
import project.xunolan.karate.service.hooks.HookService;
import project.xunolan.karate.service.hooks.hookImpl.FeatureRecordRuntimeHook;
import project.xunolan.service.ScreenRecorderService;

import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@Service
public class FeatureStartService {
    public final static ThreadLocal<Session> currentlyUseSession = new ThreadLocal<>();

    @Autowired
    HookService hookService;

    private static boolean useResource = false;

    private Feature resolveScript(Script script) {
        if(useResource){
            return resolveByResource(script);
        } else {
            return resolveByTempFile(script);
        }
    }

    private Feature resolveByTempFile(Script script){
        String resourcePath = "src/main/resources/tmp";
        File tmpDirectory = new File(resourcePath);
        // 确保 tmp 目录存在，如果不存在则创建
        if (!tmpDirectory.exists()) {
            tmpDirectory.mkdirs(); // 创建 tmp 目录
        }
        try {
            // 在 tmp 目录下创建一个临时文件
            Path tmpFilePath =  Files.createTempFile(tmpDirectory.toPath(), script.getUsecaseId() + "-" + script.getId(), ".feature");
            String scriptData = script.getData();
            File file = tmpFilePath.toFile();
            MemoryResource memoryResource = new MemoryResource(file, scriptData);
            if(!file.delete()){
                log.error("Failed to delete file {}", file.getAbsolutePath());
            }
            return Feature.read(memoryResource);
        }catch (IOException e){
            log.error(e.getMessage());
            return null;
        }
    }

    private Feature resolveByResource(Script script){
        String scriptData = script.getData();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(scriptData.getBytes(StandardCharsets.UTF_8));
        return Feature.read(new ResourceAdapter(byteArrayInputStream));
    }

    public void runScript(Session session, Script script, Boolean withRecording) {
        // 设置自定义属性
        session.getUserProperties().put("usecaseId", script.getUsecaseId());
        session.getUserProperties().put("scriptId", script.getId());
        try {
            //基于script构造feature；
            Feature feature = resolveScript(script);
            if (feature == null) {
                return;
            }
            runKarateFeature(feature, session, withRecording);
            session.getUserProperties().remove("usecaseId");
            session.getUserProperties().remove("scriptId");
        } catch (Exception e) {
            log.error("Error running Karate feature", e);
        } finally {
            currentlyUseSession.remove();
        }
    }
    
    private void runKarateFeature(Feature feature, Session session, Boolean withRecording){
        currentlyUseSession.set(session);
        FeatureCall featureCall = new FeatureCall(feature);
        
        // 根据是否需要录制选择不同的钩子
        List<RuntimeHook> hooks;
        if(withRecording != null && withRecording){
            hooks = hookService.getRecordHooks();
        } else {
            hooks = hookService.getCommonHooks();
        }

        Runner.Builder customizeBuilder = Runner.builder().hooks(hooks) //保留配置hook
                //显式设置单线程（确保 ThreadLocal 有效）
                .threads(1)
                //禁用所有报告功能。当前仅考虑性能？之后如果有生成报告需求的话可能要移出。
                .outputHtmlReport(false)
                .outputJunitXml(false)
                .outputCucumberJson(false)
                .backupReportDir(false)
                //直接传入 Feature
                .features(feature);
        //设置forTempUse，为了使当前线程与实际FeatureRuntime执行线程在同一线程中，使得ThreadLocal有效。

        //这里可以存放多个featureCall。
        FeatureRuntime featureRuntime = FeatureRuntime.of(new Suite(customizeBuilder), featureCall, null);
        featureRuntime.run();
        currentlyUseSession.remove();
    }
}