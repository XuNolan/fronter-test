package project.xunolan.websocket.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket")
public class WebSocketServer {
    //todo:session和连接管理；

    static public Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        log.info("session open: id {}", session.getId());
        sessionMap.put(session.getId(), session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("message received: {}", message);
    }

    public void sendMessage(Session session, String message) throws IOException {
        log.info("session sendText: id {}, text {}", session.getId(), message);
        session.getBasicRemote().sendText(message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket 发生错误，{}", error.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("websocket close: id {}, reason {}", session.getId(), closeReason);
    }

}