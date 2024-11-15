package project.xunolan.web.entity.recv;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import project.xunolan.web.entity.recv.impl.*;


@ToString
@Getter
@AllArgsConstructor
public enum RecvContentType {
    controll_heartbeat(RecvMsgType.heartbeat, Heartbeat.class, "heartbeat"),
    request_feature(RecvMsgType.request, RequestFeature.class ,"request_feature"),

    feature_start(RecvMsgType.process, ProcessScenarioStart.class, "feature_start"),
    feature_stop(RecvMsgType.process, ProcessScenarioStop.class, "feature_stop"),
    feature_replay(RecvMsgType.process, ProcessScenarioReplay.class, "feature_replay"),

    ;
    final RecvMsgType parentType;
    final Class<? extends RecvMsgBase> classProto;
    final String contentType;

    public static Class<? extends RecvMsgBase> getRecvClazzByType(String msgTypeStr, String contentTypeStr) {
        for(RecvContentType recvContentType : RecvContentType.values()) {
            if(msgTypeStr.equals(recvContentType.parentType.getMsgType()) && contentTypeStr.equals(recvContentType.contentType)) {
                if(recvContentType.classProto != null){
                    return recvContentType.classProto;
                }
            }
        }
        return null;
    }

    public static RecvMsgBase parseRawContent(String rawContent, String msgType, String contentType) {
        return JSON.parseObject(rawContent, RecvContentType.getRecvClazzByType(msgType, contentType));
    }
}
