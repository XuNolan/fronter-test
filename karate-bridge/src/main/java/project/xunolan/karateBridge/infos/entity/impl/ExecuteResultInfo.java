package project.xunolan.karateBridge.infos.entity.impl;

import com.alibaba.fastjson.JSON;
import com.intuit.karate.core.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    public String constructExecuuteResultInfo(){
        return JSON.toJSONString(this);
    }
}
