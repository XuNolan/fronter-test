package project.xunolan.web.entity.send.entity.impl;

import com.alibaba.fastjson.JSON;
import com.intuit.karate.core.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import project.xunolan.web.entity.send.entity.SendMsgBase;
import project.xunolan.web.entity.send.entity.SendMsgType;
import project.xunolan.web.service.WebSocketMessageDispatcher;
import project.xunolan.web.utils.BeanUtils;

import javax.websocket.Session;
import java.io.Serializable;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@Accessors(chain = true)
public class ExecuteResultInfo extends SendMsgBase implements Serializable {
    private String featureId;
    private String scenarioId;
    private String stepId;
    private String status;
    private String errorMsg;
    private String startTime;
    private String endTime;

    public static ExecuteResultInfo fromResult(Result result) {
        return ExecuteResultInfo.builder().status(result.getStatus())
                .errorMsg(result.getErrorMessage())
                .startTime(new Timestamp(result.getStartTime()).toString())
                .endTime(new Timestamp(result.getEndTime()).toString())
                .build();
    }

    public void constructAndSendExecuteResultInfo(Session session){
        String content = JSON.toJSONString(this);
        String type = SendMsgType.ExecuteInfoMsg.getMsgType();
        BeanUtils.getBean(WebSocketMessageDispatcher.class).OnSend(session, type, content);
    }
}
