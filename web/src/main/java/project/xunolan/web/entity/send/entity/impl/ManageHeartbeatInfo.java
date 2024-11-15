package project.xunolan.web.entity.send.entity.impl;

import com.alibaba.fastjson.JSON;
import project.xunolan.web.entity.send.entity.SendMsgBase;
import project.xunolan.web.entity.send.entity.SendMsgType;
import project.xunolan.web.service.WebSocketMessageDispatcher;
import project.xunolan.web.utils.BeanUtils;

import java.io.Serializable;

public class ManageHeartbeatInfo extends SendMsgBase implements Serializable {
    static public void constructAndSendHeartbeat(){
        String content = JSON.toJSONString(null);
        String type = SendMsgType.ManageHeartbeatInfoMsg.getMsgType();
        BeanUtils.getBean(WebSocketMessageDispatcher.class).OnSend(type, content);
    }
}
