package project.xunolan.web.controller;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.xunolan.common.entity.BasicResultVO;
import project.xunolan.common.enums.RespStatusEnum;
import project.xunolan.web.amisEntity.aspect.AmisResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 录制配置管理（针对 Monte 屏幕录制）
 * - 提供获取/保存全局配置的接口
 * - 配置以 JSON 形式持久化，可选 DB（默认）或本地文件
 */
@Slf4j
@RestController
@RequestMapping("/record")
public class RecordingConfigController {

    private static final Path CONFIG_FILE_PATH = Paths.get("config", "recording-config.json");

    /**
     * 获取当前录制配置
     * GET /record/config
     */
    @AmisResult
    @GetMapping("/config")
    public BasicResultVO<Map<String, Object>> getConfig() {
        try {
            Map<String, Object> config;
            if (Files.exists(CONFIG_FILE_PATH)) {
                String json = new String(Files.readAllBytes(CONFIG_FILE_PATH), StandardCharsets.UTF_8);
                config = JSON.parseObject(json, Map.class);
            } else {
                config = getDefaultConfig();
            }
            return new BasicResultVO<>(RespStatusEnum.SUCCESS, null, config);
        } catch (Exception e) {
            log.error("Failed to load recording config", e);
            return new BasicResultVO<>(RespStatusEnum.FAIL, "Failed to load recording config: " + e.getMessage(), null);
        }
    }

    /**
     * 保存录制配置
     * POST /record/config
     */
    @AmisResult
    @PostMapping("/config")
    public BasicResultVO<Void> saveConfig(@RequestBody Map<String, Object> config) {
        try {
            // 仅将配置保存到本地 JSON 文件
            if (!Files.exists(CONFIG_FILE_PATH.getParent())) {
                Files.createDirectories(CONFIG_FILE_PATH.getParent());
            }
            String json = JSON.toJSONString(config);
            Files.write(CONFIG_FILE_PATH, json.getBytes(StandardCharsets.UTF_8));
            return new BasicResultVO<>(RespStatusEnum.SUCCESS, "Saved", null);
        } catch (Exception e) {
            log.error("Failed to save recording config", e);
            return new BasicResultVO<>(RespStatusEnum.FAIL, "Failed to save recording config: " + e.getMessage(), null);
        }
    }

    private Map<String, Object> getDefaultConfig() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("qualityPreset", "medium"); // low/medium/high
        cfg.put("format", "avi"); // avi/mp4/webm
        cfg.put("storage", "db"); // db/local
        cfg.put("recordArea", "fullscreen");
        cfg.put("audio", false);
        // 可扩展: 自定义帧率/质量
        return cfg;
    }

    // 供服务端读取用的 DTO（仅用于文档提示）
    @Data
    public static class RecordingConfigDTO {
        private String qualityPreset; // low/medium/high
        private String format; // avi/mp4/webm
        private String storage; // db/local
        private String recordArea; // fullscreen 等
        private boolean audio; // 默认 false
        private Integer frameRate; // 可选自定义
        private Float quality; // 可选自定义 0.1-1.0
    }
}