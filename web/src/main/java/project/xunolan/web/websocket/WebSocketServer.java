package project.xunolan.web.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.xunolan.websocket.entity.recv.RecvEntity;
import project.xunolan.websocket.entity.recv.RecvMsgBase;
import project.xunolan.websocket.entity.recv.RecvMsgType;
import project.xunolan.websocket.entity.send.SendEntity;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket")
public class WebSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        log.info("session open: id {}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("message received: {}", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket 发生错误，{}", error.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("websocket close: id {}, reason {}", session.getId(), closeReason);
    }

    public static void OnRecv(Session session, String message) {
        RecvEntity recvEntity = JSON.parseObject(message, RecvEntity.class);
        RecvMsgBase recvMsgBase = RecvMsgType.parseRawContent(recvEntity.getContent(), recvEntity.getMsgType());
        if(recvMsgBase != null) {
            recvMsgBase.processMsg(session);
        }else {
            log.error("recv empty or unknown msg type, message {}, parse result:{}", message, recvEntity);
        }
    }

    public static void OnSend(Session session, SendEntity sendEntity) {
        try {
            session.getBasicRemote().sendText(JSON.toJSONString(sendEntity));
        } catch (IOException e) {
            log.error("send message error ,type : {} , message content: {}, error info :{} ",sendEntity.msgType, sendEntity.content, e.getMessage());
        }
    }
}