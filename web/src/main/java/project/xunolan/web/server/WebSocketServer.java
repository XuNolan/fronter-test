package project.xunolan.web.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.web.service.WebSocketMessageDispatcher;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket")
public class WebSocketServer {

    private Session session;

    @Autowired
    public WebSocketMessageDispatcher webSocketMessageDispatcher;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        webSocketMessageDispatcher.OnRecv(message);

    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket 发生错误，{}", error.getMessage());
    }


}
