package project.xunolan.web.entity.send.entity.impl;

import com.alibaba.fastjson.JSON;
import com.intuit.karate.core.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import project.xunolan.web.entity.send.entity.SendMsgType;
import project.xunolan.web.service.WebSocketMessageDispatcher;
import project.xunolan.web.utils.BeanUtils;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@Accessors(chain = true)
public class ExecuteResultInfo {

    private String stepId;
    private String status;
    private String errorMsg;
    private String startTime;
    private String endTime;

    public static ExecuteResultInfo fromResult(Result result) {
        return ExecuteResultInfo.builder().status(result.getStatus())
                .errorMsg(result.getError().getMessage())
                .startTime(new Timestamp(result.getStartTime()).toString())
                .endTime(new Timestamp(result.getEndTime()).toString())
                .build();
    }

    public void constructAndSendExecuteResultInfo(){
        String content = JSON.toJSONString(this);
        String type = SendMsgType.ExecuteInfoMsg.getMsgType();
        BeanUtils.getBean(WebSocketMessageDispatcher.class).OnSend(type, content);
    }
}
