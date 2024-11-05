package project.xunolan.karateBridge.infos.service;

import com.intuit.karate.core.Feature;
import com.intuit.karate.core.Scenario;
import com.intuit.karate.core.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import project.xunolan.karateBridge.infos.entity.impl.ScenarioInfo;
import project.xunolan.karateBridge.infos.entity.impl.StepInfo;
import project.xunolan.karateBridge.infos.entity.impl.FeatureInfo;

import java.io.File;
import java.util.*;

@Slf4j
public class FeatureService {
    private final static String featureDirPath = "src/main/resources/features";
    private static File dir;
    //除了保存此处映射之外，还要保存至少每次读取的唯一性。

    @Getter
    private final static Map<String,  Feature> featureMap = new HashMap<>(); //filename - feature

    //暂时使用递增id及拼接。
    @Getter
    private final static Map<String, FeatureInfo> featureInfoMap = new HashMap<>();
    @Getter
    private final static Map<String, ScenarioInfo> scenarioInfoMap = new HashMap<>();
    @Getter
    private final static Map<String, StepInfo> stepInfoMap = new HashMap<>();

    private final static String ID_INTERVAL = "_";

    //懒加载。不应当放在构造函数内部，应当自己提出，构造函数仅完成了一次调用。
    public static void reloadFeatureResource(){
        //load
        dir = new File(featureDirPath);
        if(!dir.exists() || !dir.isDirectory()){
            log.error("featureDirPath not exist or not a dir ,error info :{} ",featureDirPath);
        }
    }

    public static void reConstructFeatureMaps(){
        featureMap.clear();
        featureInfoMap.clear();
        scenarioInfoMap.clear();
        stepInfoMap.clear();
        for(int featureNum = 0; featureNum < Objects.requireNonNull(dir.listFiles()).length; featureNum++){
            File file = Objects.requireNonNull(dir.listFiles())[featureNum];
            Feature feature = Feature.read(file);
            featureMap.put(file.getName(), feature);
            FeatureInfo featureInfo = FeatureInfo.builder().featureId(String.valueOf(featureNum)).
                    featureName(feature.getName()).fileName(feature.getResource().getFileNameWithoutExtension()).feature(feature).build();
            featureInfo.setScenarios(new ArrayList<>());
            for(int scenarioNum = 0; scenarioNum < feature.getSections().size(); scenarioNum++){
                Scenario scenario = feature.getSections().get(scenarioNum).getScenario();
                ScenarioInfo scenarioInfo = ScenarioInfo.builder().ScenarioId(featureInfo.getFeatureId() + ID_INTERVAL + scenarioNum)
                        .scenarioName(scenario.getName()).build();
                scenarioInfo.setSteps(new ArrayList<>());
                featureInfo.getScenarios().add(scenarioInfo);
                for(int stepNum = 0; stepNum < scenario.getSteps().size(); stepNum++){
                    Step step = scenario.getSteps().get(stepNum);
                    StepInfo stepInfo = StepInfo.builder().StepId(scenarioInfo.getScenarioId() + ID_INTERVAL + stepNum)
                            .prefix(step.getPrefix()).stepText(step.getText()).build();
                    scenarioInfo.getSteps().add(stepInfo);
                }
            }
        }
    }

}
