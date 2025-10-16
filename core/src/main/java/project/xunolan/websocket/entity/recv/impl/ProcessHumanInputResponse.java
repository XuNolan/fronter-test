package project.xunolan.websocket.entity.recv.impl;

import lombok.Data;
import project.xunolan.karate.human.HumanInputGateway;
import project.xunolan.websocket.entity.recv.RecvMsgBase;

import javax.websocket.Session;

@Data
public class ProcessHumanInputResponse extends RecvMsgBase {
    private String requestId;
    private String value;

    @Override
    public void processMsg(Session session) {
        System.out.println("ProcessHumanInputResponse received - requestId: " + requestId + ", value: " + value);
        HumanInputGateway.complete(requestId, value);
        System.out.println("HumanInputGateway.complete called for requestId: " + requestId);
    }
}


