package project.xunolan.websocket.entity.recv.impl;

import project.xunolan.websocket.entity.recv.RecvMsgBase;

import javax.websocket.Session;

public class Heartbeat extends RecvMsgBase {
    //内容为空；

    @Override
    public void processMsg(Session session) {
        ManageHeartbeatInfo.constructAndSendHeartbeat(session);
    }
}
