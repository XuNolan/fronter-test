package project.xunolan.karateBridge.infos.service.entity;

import com.alibaba.fastjson.JSON;
import com.intuit.karate.core.Feature;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import project.xunolan.karateBridge.infos.service.FeatureService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class FeatureInfo {
    String featureId;
    String featureName;
    String fileName;
    List<ScenarioInfo> scenarios;
    Feature feature;

    public static List<FeatureInfo> constructFeatureInfo(boolean needReload) {
        if(needReload || FeatureService.getFeatureMap().isEmpty()){
            FeatureService.reloadFeatureResource();
            FeatureService.reConstructFeatureMaps();
        }
        return new ArrayList<>(FeatureService.getFeatureInfoMap().values());
    }
}
