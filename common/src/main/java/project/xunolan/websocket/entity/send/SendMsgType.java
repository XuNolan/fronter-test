package project.xunolan.websocket.entity.send;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;

@ToString
@Getter
@AllArgsConstructor
public enum SendMsgType {
    FeatureInfoMsg(SendFeatureInfo.class, "featureInfos"),
    ExecuteInfoMsg(ExecuteResultInfo.class, "executeInfos"), //hasn't complete
    ManageHeartbeatInfoMsg(ManageHeartbeatInfo.class, "heartbeat"),

    ;
    final Class<? extends SendMsgBase> classProto;
    final String msgType;

    //FeatureInfo是没用了。但是估计其他地方有用？或者至少在Recv的反序列化有用？
    public static SendMsgBase getInstanceByType(String msgTypeStr) {
        for(SendMsgType sendMsgType : SendMsgType.values()) {
            if(msgTypeStr.equals(sendMsgType.msgType)) {
                if(sendMsgType.classProto != null){
                    try {
                        return sendMsgType.classProto.getDeclaredConstructor().newInstance();
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