package project.xunolan.karateBridge.infos.entity.impl;

import com.alibaba.fastjson.JSON;
import com.intuit.karate.core.Feature;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import project.xunolan.karateBridge.infos.entity.SendEntity;
import project.xunolan.karateBridge.infos.entity.SendMessageType;
import project.xunolan.karateBridge.infos.entity.SendMsgBase;
import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.karateBridge.infos.utils.BeanUtils;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class FeatureInfo extends SendMsgBase implements Serializable {
    String featureId;
    String featureName;
    String fileName;
    List<ScenarioInfo> scenarios;

    transient Feature feature;

    public static String constructAndSendFeatureInfo(boolean needReload){
        if(needReload || FeatureService.getFeatureMap().isEmpty()){
            FeatureService.reloadFeatureResource();
            FeatureService.reConstructFeatureMaps();
        }
        List<FeatureInfo> featureInfoList = new ArrayList<>(FeatureService.getFeatureInfoMap().values());
         JSON.toJSONString(featureInfoList);
    }
}
