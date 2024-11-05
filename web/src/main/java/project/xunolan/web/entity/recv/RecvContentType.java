package project.xunolan.web.entity.recv;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import project.xunolan.web.entity.recv.impl.ProcessScenarioReplay;
import project.xunolan.web.entity.recv.impl.ProcessScenarioStart;
import project.xunolan.web.entity.recv.impl.ProcessScenarioStop;
import project.xunolan.web.entity.recv.impl.RequestFeature;


@ToString
@Getter
@AllArgsConstructor
public enum RecvContentType {
    request_feature(RecvMessageType.request, RequestFeature.class ,"request_feature"),

    scenario_start(RecvMessageType.process, ProcessScenarioStart.class, "scenario_start"),
    scenario_stop(RecvMessageType.process, ProcessScenarioStop.class, "scenario_stop"),
    scenario_replay(RecvMessageType.process, ProcessScenarioReplay.class, "scenario_replay"),

    ;
    final RecvMessageType parentType;
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
