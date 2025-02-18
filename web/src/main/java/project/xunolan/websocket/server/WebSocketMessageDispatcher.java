package project.xunolan.websocket.server;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.websocket.entity.recv.RecvEntity;
import project.xunolan.websocket.entity.recv.RecvMsgBase;
import project.xunolan.websocket.entity.recv.RecvMsgType;
import project.xunolan.websocket.entity.send.SendEntity;

import javax.websocket.Session;
import java.io.IOException;

@Service
@Slf4j
public class WebSocketMessageDispatcher {

    public WebSocketServer webSocketServer;

    @Autowired
    public void setWebSocketServer(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    public void OnRecv(Session session, String message) {
        RecvEntity recvEntity = JSON.parseObject(message, RecvEntity.class);
        RecvMsgBase recvMsgBase = RecvMsgType.parseRawContent(recvEntity.getContent(), recvEntity.getMsgType());
        recvMsgBase.processMsg(session);
    }

    public void OnSend(Session session, String type, String content) {
        try {
            SendEntity sendEntity = SendEntity.builder().msgType(type).content(content).build();
            webSocketServer.sendMessage(session, JSON.toJSONString(sendEntity));
        } catch (IOException e) {
            log.error("send message error ,type : {} , message content: {}, error info :{} ",type, content, e.getMessage());
        }
    }
}