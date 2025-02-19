package project.xunolan.websocket.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.websocket.Session;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
public class SocketPackage {
    public static String QUEUE_CLASS_NAME = "SendEntity";

    public Session session;

    public Object sendEntity;

    public static final BlockingQueue<SocketPackage> executeLogQueue = new LinkedBlockingQueue<>(65535);

    public static void sendToExecuteLogQueue(SocketPackage socketPackage) {
        executeLogQueue.add(socketPackage);
    }

    public static SocketPackage takeFromExecuteLogQueue() throws InterruptedException {
        return executeLogQueue.take();
    }

}
