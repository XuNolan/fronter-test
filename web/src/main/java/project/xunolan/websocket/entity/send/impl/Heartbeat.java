package project.xunolan.websocket.entity.send.impl;

import com.alibaba.fastjson.JSON;
import project.xunolan.websocket.entity.send.SendMsgBase;
import project.xunolan.websocket.entity.send.SendMsgType;
import project.xunolan.websocket.server.WebSocketMessageDispatcher;
import project.xunolan.websocket.utils.BeanUtils;

import javax.websocket.Session;
import java.io.Serializable;

public class Heartbeat extends SendMsgBase implements Serializable {
    static public void constructAndSendHeartbeat(Session session){
        String content = JSON.toJSONString(null);
        String type = SendMsgType.ManageHeartbeatInfoMsg.getMsgType();
        BeanUtils.getBean(WebSocketMessageDispatcher.class).OnSend(session, type, content);
    }
}