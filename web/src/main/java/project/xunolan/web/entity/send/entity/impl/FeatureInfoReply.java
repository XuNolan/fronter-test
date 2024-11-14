package project.xunolan.web.entity.send.entity.impl;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import project.xunolan.karateBridge.infos.service.entity.FeatureInfo;
import project.xunolan.karateBridge.infos.service.entity.ScenarioInfo;
import project.xunolan.web.entity.send.entity.SendMsgType;
import project.xunolan.web.entity.send.entity.SendMsgBase;
import project.xunolan.web.service.WebSocketMessageDispatcher;
import project.xunolan.web.utils.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class FeatureInfoReply extends SendMsgBase implements Serializable {
    String featureId;
    String featureName;
    String fileName;
    List<ScenarioInfo> scenarios;


    //要不在这里手动降次？这样就完全没有问题了。
    //还是先按照上面的逻辑来吧。直接用上面的entity。因为bridge对web可见。
    //也就是，仅在这里小小地耦合了一下。其他可见的仅有service。
    public static FeatureInfoReply fromFeatureInfo(FeatureInfo featureInfo) {
        return FeatureInfoReply.builder().featureId(featureInfo.getFeatureId())
                .featureName(featureInfo.getFeatureName())
                .fileName(featureInfo.getFileName())
                .scenarios(featureInfo.getScenarios()).build();
    }

    public static void constructAndSendFeatureInfoReply(boolean needReload){
        List<FeatureInfo> featureInfoList = FeatureInfo.constructFeatureInfo(needReload);
        List<FeatureInfoReply> featureInfoReplyList = new ArrayList<>();
        for(FeatureInfo featureInfo : featureInfoList){
            featureInfoReplyList.add(FeatureInfoReply.fromFeatureInfo(featureInfo));
        }
        String content = JSON.toJSONString(featureInfoList);
        String type = SendMsgType.FeatureInfoMsg.getMsgType();
        BeanUtils.getBean(WebSocketMessageDispatcher.class).OnSend(type, content);
    }
}
