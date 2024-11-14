package project.xunolan.web.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.xunolan.web.entity.send.entity.SendEntity;
import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.web.entity.recv.RecvContentType;
import project.xunolan.web.entity.recv.RecvEntity;
import project.xunolan.web.entity.recv.RecvMsgBase;
import project.xunolan.web.server.WebSocketServer;

import java.io.IOException;

@Service
@Slf4j
public class WebSocketMessageDispatcher {

    @Autowired
    public WebSocketServer webSocketServer;

    @Autowired
    public FeatureService featureService;


    public void OnRecv(String message) {
        RecvEntity recvEntity = JSON.parseObject(message, RecvEntity.class);
        RecvMsgBase recvMsgBase = RecvContentType.parseRawContent(recvEntity.getContent(), recvEntity.getMsgType(), recvEntity.getContentType());
        recvMsgBase.processMsg();
    }

    public void OnSend(String type, String content) {
        try {
            SendEntity sendEntity = SendEntity.builder().msgType(type).content(content).build();
            webSocketServer.sendMessage(JSON.toJSONString(sendEntity));
        } catch (IOException e) {
            log.error("send message error ,type : {} , message content: {}, error info :{} ",type, content, e.getMessage());
        }
    }
}
