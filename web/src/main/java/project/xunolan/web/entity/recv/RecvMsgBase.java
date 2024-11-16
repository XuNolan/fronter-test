package project.xunolan.web.entity.recv;


import javax.websocket.Session;

public abstract class RecvMsgBase {

    abstract public void processMsg(Session session);
}
