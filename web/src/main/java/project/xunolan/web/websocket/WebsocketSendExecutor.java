package project.xunolan.web.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import project.xunolan.websocket.queue.SocketPackage;
import project.xunolan.websocket.queue.ThreadPoolUtils;
import project.xunolan.websocket.entity.send.SendEntity;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class WebsocketSendExecutor implements CommandLineRunner {

    @Resource
    ThreadPoolUtils threadPoolUtils;

    //todo:锁；

    @Override
    public void run(String... args) {
        //execute log only
        Runnable processExecuteLog = ()->{
            while(!Thread.interrupted()) {
                try{
                    SocketPackage socketPackage = SocketPackage.takeFromExecuteLogQueue();
                    threadPoolUtils.getExecuteLogExecutor().execute(()->{
                        WebSocketServer.OnSend(socketPackage.session, (SendEntity) socketPackage.sendEntity);
                        log.info("send package, sessionId {}, msg {}", socketPackage.session, socketPackage.sendEntity);
                    });
                } catch (InterruptedException e) {
                    log.error("send package thread err, {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        };
        Runnable processScenarioInfo = ()->{
            while(!Thread.interrupted()) {
                try{
                    SocketPackage socketPackage = SocketPackage.takeFromExecuteLogQueue();
                    threadPoolUtils.getScenarioInfoExecutor().execute(()->{
                        WebSocketServer.OnSend(socketPackage.session, (SendEntity) socketPackage.sendEntity);
                        log.info("send package, sessionId {}, msg {}", socketPackage.session, socketPackage.sendEntity);
                    });
                } catch (InterruptedException e) {
                    log.error("send package thread err, {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        };
        Executors.newSingleThreadExecutor().execute(processExecuteLog);
        Executors.newSingleThreadExecutor().execute(processScenarioInfo);
    }
}
