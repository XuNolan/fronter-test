package project.xunolan.websocket.entity.recv;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import project.xunolan.websocket.entity.recv.impl.ProcessScriptStart;

@ToString
@Getter
@AllArgsConstructor
public enum RecvMsgType {
    script_start(ProcessScriptStart.class, "SCRIPTSTART"),
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