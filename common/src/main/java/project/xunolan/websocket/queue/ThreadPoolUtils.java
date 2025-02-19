package project.xunolan.websocket.queue;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ThreadPoolUtils {

    private static volatile ThreadPoolTaskExecutor POOL;

    public ThreadPoolTaskExecutor getInstance() {
        if (Objects.isNull(POOL)) {
            synchronized (this) {
                if (Objects.isNull(POOL)) {
                    POOL = new ThreadPoolTaskExecutor();
                    POOL.setCorePoolSize(4);
                    POOL.setMaxPoolSize(4);
                    POOL.setQueueCapacity(65535);
                    POOL.setKeepAliveSeconds(60);
                    POOL.setThreadNamePrefix("thread-");
                    POOL.setDaemon(true);
                    POOL.initialize();
                }
            }
        }
        return POOL;
    }
}
