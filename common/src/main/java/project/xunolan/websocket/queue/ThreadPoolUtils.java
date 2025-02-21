package project.xunolan.websocket.queue;

import lombok.Getter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ThreadPoolUtils {

    @Getter
    private static volatile ThreadPoolTaskExecutor executeLogExecutor;
    @Getter
    private static volatile ThreadPoolTaskExecutor scenarioInfoExecutor;


    private static final int small_thread_core_pool_size = 4;
    private static final int small_thread_max_pool_size = 4;
    private static final int small_queue_capacity = 512;
    private static final int large_queue_capacity = 65535;

    private static ThreadPoolTaskExecutor initQueueSendExecutor(String threadNamePrefix){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(small_thread_core_pool_size);
        threadPoolTaskExecutor.setMaxPoolSize(small_thread_max_pool_size);
        threadPoolTaskExecutor.setQueueCapacity(large_queue_capacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        threadPoolTaskExecutor.setDaemon(true);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    static {
        executeLogExecutor = initQueueSendExecutor("executeLog");
        scenarioInfoExecutor = initQueueSendExecutor("scenarioInfo");
    }
}
