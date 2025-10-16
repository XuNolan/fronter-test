package project.xunolan.karate.human;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import project.xunolan.karate.service.FeatureStartService;
import project.xunolan.websocket.entity.send.SendEntity;
import project.xunolan.websocket.queue.SocketPackage;

import javax.websocket.Session;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 人机输入网关：在 Karate 脚本中可通过 Java.type 调用，阻塞等待前端输入。
 */
public class HumanInputGateway {

    private static final Map<String, CompletableFuture<String>> REQUEST_FUTURES = new ConcurrentHashMap<>();

    /**
     * 发送人机输入请求，并阻塞等待前端回传。
     * @param prompt 提示文案
     * @param type 输入类型：input|select|radio
     * @param options 可选项（当 type=select/radio 时使用）
     * @param defaultValue 默认值
     * @param timeoutMs 超时时间（毫秒）
     * @return 用户输入值（字符串）
     */
    public static String request(String prompt, String type, Object options, String defaultValue, long timeoutMs) {
        Session session = FeatureStartService.currentlyUseSession.get();
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("No websocket session bound in current thread");
        }

        String requestId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        REQUEST_FUTURES.put(requestId, future);
        
        System.out.println("[" + new java.util.Date() + "] HumanInputGateway.request - created request with ID: " + requestId);

        JSONObject payload = new JSONObject();
        payload.put("requestId", requestId);
        payload.put("prompt", prompt);
        payload.put("type", type == null ? "input" : type);
        if (options != null) {
            payload.put("options", options);
        }
        payload.put("defaultValue", defaultValue);
        payload.put("timeoutMs", timeoutMs);

        String sendJson = JSON.toJSONString(new SendEntity("human_input_request", payload.toJSONString()));
        SocketPackage.sendToExecuteLogQueue(new SocketPackage(session, sendJson));
        
        System.out.println("[" + new java.util.Date() + "] HumanInputGateway.request - message sent, waiting for response...");

        try {
            if (timeoutMs <= 0) {
                return future.get();
            } else {
                String result = future.get(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
                System.out.println("[" + new java.util.Date() + "] HumanInputGateway.request - received response: " + result);
                return result;
            }
        } catch (Exception ex) {
            System.out.println("[" + new java.util.Date() + "] HumanInputGateway.request - timeout or error: " + ex.getMessage());
            future.cancel(true);
            REQUEST_FUTURES.remove(requestId);
            throw new RuntimeException("human input timeout or interrupted: " + ex.getMessage(), ex);
        } finally {
            REQUEST_FUTURES.remove(requestId);
        }
    }

    /**
     * 由 WebSocket 接收器调用：完成指定 requestId 的输入。
     */
    public static void complete(String requestId, String value) {
        System.out.println("HumanInputGateway.complete called - requestId: " + requestId + ", value: " + value);
        CompletableFuture<String> future = REQUEST_FUTURES.get(requestId);
        if (future != null && !future.isDone()) {
            System.out.println("Found future for requestId: " + requestId + ", completing with value: " + value);
            future.complete(value);
        } else {
            System.out.println("No future found for requestId: " + requestId + " or future already completed");
        }
    }
}


