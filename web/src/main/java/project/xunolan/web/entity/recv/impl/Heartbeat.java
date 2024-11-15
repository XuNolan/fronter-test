package project.xunolan.web.entity.recv.impl;

import project.xunolan.web.entity.recv.RecvMsgBase;
import project.xunolan.web.entity.send.entity.impl.ManageHeartbeatInfo;

import javax.websocket.Session;

public class Heartbeat extends RecvMsgBase {

    @Override
    public void processMsg() {
        ManageHeartbeatInfo.constructAndSendHeartbeat();
    }
}