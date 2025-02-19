package project.xunolan.websocket.entity.send;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import project.xunolan.websocket.entity.send.impl.ExecuteResultInfo;
import project.xunolan.websocket.entity.send.impl.KarateFeatureInfo;

import java.lang.reflect.InvocationTargetException;

@ToString
@Getter
@AllArgsConstructor
public enum SendMsgType {
    ExecuteInfoMsg(ExecuteResultInfo.class, "executeInfos"), //hasn't complete
    KarateFeatureInfoMsg(KarateFeatureInfo.class, "KarateFeatureInfos"), //hasn't complete
    ;
    final Class<? extends SendMsgBase> classProto;
    final String msgType;

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