package project.xunolan.websocket.queue;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ThreadPoolUtils {

    private static volatile ThreadPoolTaskExecutor executeLogExecutor;
    private static volatile ThreadPoolTaskExecutor scenarioInfoExecutor;

    public ThreadPoolTaskExecutor getExecuteLogExecutor() {
        if (Objects.isNull(executeLogExecutor)) {
            synchronized (this) {
                if (Objects.isNull(executeLogExecutor)) {
                    executeLogExecutor = new ThreadPoolTaskExecutor();
                    executeLogExecutor.setCorePoolSize(4);
                    executeLogExecutor.setMaxPoolSize(4);
                    executeLogExecutor.setQueueCapacity(65535);
                    executeLogExecutor.setKeepAliveSeconds(60);
                    executeLogExecutor.setThreadNamePrefix("thread-");
                    executeLogExecutor.setDaemon(true);
                    executeLogExecutor.initialize();
                }
            }
        }
        return executeLogExecutor;
    }

    public ThreadPoolTaskExecutor getScenarioInfoExecutor() {
        if (Objects.isNull(scenarioInfoExecutor)) {
            synchronized (this) {
                if (Objects.isNull(scenarioInfoExecutor)) {
                    scenarioInfoExecutor = new ThreadPoolTaskExecutor();
                    scenarioInfoExecutor.setCorePoolSize(4);
                    scenarioInfoExecutor.setMaxPoolSize(4);
                    scenarioInfoExecutor.setQueueCapacity(65535);
                    scenarioInfoExecutor.setKeepAliveSeconds(60);
                    scenarioInfoExecutor.setThreadNamePrefix("thread-");
                    scenarioInfoExecutor.setDaemon(true);
                    scenarioInfoExecutor.initialize();
                }
            }
        }
        return scenarioInfoExecutor;
    }
}
