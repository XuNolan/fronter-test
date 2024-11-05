package project.xunolan.web.entity.send.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import project.xunolan.web.entity.send.entity.impl.FeatureInfoReply;

import java.lang.reflect.InvocationTargetException;

@ToString
@Getter
@AllArgsConstructor
public enum SendMessageType {
    FeatureInfoMsg(FeatureInfoReply.class, "featureInfos"),
    ExecuteInfoMsg(null, "executeInfos"), //hasn't complete

    ;
    final Class<? extends SendMsgBase> classProto;
    final String msgTypeStr;

    //FeatureInfo是没用了。但是估计其他地方有用？或者至少在Recv的反序列化有用？
    public static SendMsgBase getInstanceByType(String msgTypeStr) {
        for(SendMessageType sendMessageType : SendMessageType.values()) {
            if(msgTypeStr.equals(sendMessageType.msgTypeStr)) {
                if(sendMessageType.classProto != null){
                    try {
                        return sendMessageType.classProto.getDeclaredConstructor().newInstance();
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }
}
