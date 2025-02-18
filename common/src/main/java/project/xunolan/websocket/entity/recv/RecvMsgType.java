package project.xunolan.websocket.entity.recv;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import project.xunolan.websocket.entity.recv.impl.Heartbeat;
import project.xunolan.websocket.entity.recv.impl.ProcessScenarioStart;

@ToString
@Getter
@AllArgsConstructor
public enum RecvMsgType {
    Heartbeat(Heartbeat.class, "heartbeat"),
    feature_start(ProcessScenarioStart.class, "feature_start"),
    ;

    final Class<? extends RecvMsgBase> classProto;
    final String typeName;

    public static Class<? extends RecvMsgBase> getClassProto(String typeName) {
        for(RecvMsgType msgType : RecvMsgType.values()) {
            if(msgType.typeName.equals(typeName)) {
                return msgType.classProto;
            }
        }
        return null;
    }

    public static RecvMsgBase parseRawContent(String rawContent, String msgType) {
        Class<? extends RecvMsgBase> classProto = getClassProto(msgType);
        if(classProto == null)
            return null;
        return JSON.parseObject(rawContent, classProto);
    }
}