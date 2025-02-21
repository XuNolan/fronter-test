package project.xunolan.web.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import project.xunolan.websocket.queue.SocketPackage;
import project.xunolan.websocket.queue.ThreadPoolUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class WebsocketSendExecutor implements CommandLineRunner {


    @Override
    public void run(String... args) {
        //execute log only
        Runnable processExecuteLog = ()->{
            while(!Thread.interrupted()) {
                try{
                    SocketPackage socketPackage = SocketPackage.takeFromExecuteLogQueue();
                    ThreadPoolUtils.getExecuteLogExecutor().execute(()->{
                        WebSocketServer.OnSend(socketPackage.session, socketPackage.sendEntity);
                        log.info("send log package, sessionId {}, msg {}", socketPackage.session, socketPackage.sendEntity);
                    });
                } catch (InterruptedException e) {
                    log.error("send log package thread err, {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        };
        Runnable processScenarioInfo = ()->{
            while(!Thread.interrupted()) {
                try{
                    SocketPackage socketPackage = SocketPackage.takeFromScenarioInfoQueue();
                    ThreadPoolUtils.getScenarioInfoExecutor().execute(()->{ //todo：锁。且需要判断session未关闭。
                        WebSocketServer.OnSend(socketPackage.session, socketPackage.sendEntity);
                        log.info("send ScenarioInfo package, sessionId {}, msg {}", socketPackage.session, socketPackage.sendEntity);
                    });
                } catch (InterruptedException e) {
                    log.error("send ScenarioInfo package thread err, {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(processExecuteLog);
        executorService.execute(processScenarioInfo);
    }
}
