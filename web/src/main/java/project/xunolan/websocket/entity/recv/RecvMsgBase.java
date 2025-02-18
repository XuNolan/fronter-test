package project.xunolan.websocket.entity.recv;


import javax.websocket.Session;

public abstract class RecvMsgBase {
    abstract public void processMsg(Session session);
}