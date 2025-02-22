package project.xunolan.karate.service;

import com.intuit.karate.Runner;
import com.intuit.karate.Suite;
import com.intuit.karate.core.*;
import com.intuit.karate.resource.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.database.entity.Script;
import project.xunolan.karate.adapter.ResourceAdapter;
import project.xunolan.karate.service.hooks.HookService;

import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FeatureStartService {
    public final static ThreadLocal<Session> currentlyUseSession = new ThreadLocal<>();

    @Autowired
    HookService hookService;

    private static boolean useResource = false;

    private Feature resolveScript(Script script) {
        File file;
        if(useResource){
            return Feature.read(resolveByResource(script));
        } else {
           file = resolveByTempFile(script);
            if(file == null){
                log.error("resolve script file failed, file is empty");
                return null;
            }
            Feature feature =  Feature.read(file);
            if(!file.delete()){
                log.error("Failed to delete file {}", file.getAbsolutePath());
            }
            return feature;
        }
    }

    private File resolveByTempFile(Script script){
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
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(scriptData.getBytes(StandardCharsets.UTF_8))) {
                Files.copy(byteArrayInputStream, tmpFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return tmpFilePath.toFile();
        }catch (IOException e){
            log.error(e.getMessage());
            return null;
        }
    }

    private Resource resolveByResource(Script script){
        String scriptData = script.getData();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(scriptData.getBytes(StandardCharsets.UTF_8));
        return new ResourceAdapter(byteArrayInputStream);
        //主要就是用在getStream。
    }

    public void RunScript(Session session, Script script){
        //基于script构造feature；
        Feature feature = resolveScript(script);
        if(feature == null){
            return;
        }
        RunKarateFeature(feature, session);
    }

    private void RunKarateFeature(Feature feature, Session session){
        currentlyUseSession.set(session);
        FeatureCall featureCall = new FeatureCall(feature);
        Runner.Builder customizeBuilder = Runner.builder().hooks(hookService.getCommonHooks());
        //设置forTempUse，为了使当前线程与实际FeatureRuntime执行线程在同一线程中，使得ThreadLocal有效。

        //这里可以存放多个featureCall。
        FeatureRuntime featureRuntime = FeatureRuntime.of(new Suite(customizeBuilder), featureCall, null);
        featureRuntime.run();
        currentlyUseSession.remove();
    }

}