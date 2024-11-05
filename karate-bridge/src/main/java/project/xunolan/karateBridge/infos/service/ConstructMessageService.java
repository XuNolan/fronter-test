package project.xunolan.karateBridge.infos.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.karateBridge.infos.entity.SendEntity;
import project.xunolan.karateBridge.infos.entity.SendMessageType;
import project.xunolan.karateBridge.infos.entity.impl.FeatureInfo;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConstructMessageService {

    public static SendEntity constructFeatureInfo(boolean needReload){
        if(needReload || FeatureService.getFeatureMap().isEmpty()){
            FeatureService.reloadFeatureResource();
            FeatureService.reConstructFeatureMaps();
        }
        List<FeatureInfo> featureInfoList = new ArrayList<>(FeatureService.getFeatureInfoMap().values());
        String content =  JSON.toJSONString(featureInfoList);
        return SendEntity.builder().type(SendMessageType.FeatureInfoMsg.getMsgTypeStr()).content(content).build();
    }

}
