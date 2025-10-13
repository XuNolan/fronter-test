package project.xunolan.service;

import lombok.extern.slf4j.Slf4j;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

/**
 * 屏幕录制服务
 * 用于录制 Karate 测试执行时的浏览器窗口
 */
@Slf4j
@Service
public class ScreenRecorderService {
    
    private ScreenRecorder screenRecorder;
    private File currentRecordingFile;
    private final String recordingsDir = "target/recordings";

    private static final Path CONFIG_FILE_PATH = Paths.get("config", "recording-config.json");
    
    /**
     * 开始录制
     * 
     * @param scriptId 脚本ID
     * @param usecaseId 用例ID
     * @return 录像文件路径
     */
    public synchronized String startRecording(Long scriptId, Long usecaseId) throws Exception {
        if (screenRecorder != null) {
            log.warn("Recording already in progress, stopping previous recording");
            stopRecording();
        }
        
        // 确保录像目录存在
        Path recordingPath = Paths.get(recordingsDir);
        if (!Files.exists(recordingPath)) {
            Files.createDirectories(recordingPath);
        }
        
        // 生成录像文件名
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        RecordingRuntimeConfig runtimeConfig = loadRuntimeConfig();
        String fileName = String.format("test_%d_%d_%s", usecaseId, scriptId, timestamp);
        
        try {
            // 获取屏幕尺寸
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();
            
            // 配置录制参数（根据配置映射）
            screenRecorder = new CustomScreenRecorder(
                    gc,
                    gc.getBounds(),
                    // 文件格式：根据格式选择（Monte 常用 AVI）
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    // 屏幕格式：TechSmith Screen Capture 编码（与 Monte 兼容性最好）
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(runtimeConfig.frameRate),
                            QualityKey, runtimeConfig.quality,
                            KeyFrameIntervalKey, runtimeConfig.frameRate * 60),
                    // 鼠标指针格式
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                            FrameRateKey, Rational.valueOf(30)),
                    // 音频格式：无音频
                    null,
                    new File(recordingsDir),
                    fileName + selectExtension(runtimeConfig.format)
            );
            
            screenRecorder.start();
            
            log.info("Started screen recording: {}/{}", recordingsDir, fileName + selectExtension(runtimeConfig.format));
            return fileName + selectExtension(runtimeConfig.format);
            
        } catch (Exception e) {
            log.error("Failed to start screen recording", e);
            throw new Exception("Failed to start screen recording: " + e.getMessage(), e);
        }
    }
    
    /**
     * 停止录制
     * 
     * @return 录像文件
     */
    public synchronized File stopRecording() throws Exception {
        if (screenRecorder == null) {
            log.warn("No recording in progress");
            return null;
        }
        
        try {
            screenRecorder.stop();
            
            // 获取生成的文件
            currentRecordingFile = screenRecorder.getCreatedMovieFiles().get(0);
            
            log.info("Stopped screen recording: {}", currentRecordingFile.getAbsolutePath());
            
            return currentRecordingFile;
            
        } catch (Exception e) {
            log.error("Failed to stop screen recording", e);
            throw new Exception("Failed to stop screen recording: " + e.getMessage(), e);
        } finally {
            screenRecorder = null;
        }
    }
    
    /**
     * 获取当前录像文件
     */
    public File getCurrentRecordingFile() {
        return currentRecordingFile;
    }
    
    /**
     * 检查是否正在录制
     */
    public boolean isRecording() {
        return screenRecorder != null && screenRecorder.getState() == ScreenRecorder.State.RECORDING;
    }
    
    /**
     * 自定义 ScreenRecorder，用于自定义文件名
     */
    private static class CustomScreenRecorder extends ScreenRecorder {
        private final String name;
        
        public CustomScreenRecorder(GraphicsConfiguration cfg,
                                     Rectangle captureArea,
                                     Format fileFormat,
                                     Format screenFormat,
                                     Format mouseFormat,
                                     Format audioFormat,
                                     File movieFolder,
                                     String name) throws IOException, AWTException {
            super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
            this.name = name;
        }
        
        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            if (!movieFolder.exists()) {
                movieFolder.mkdirs();
            }
            return new File(movieFolder, name + "." + Registry.getInstance().getExtension(fileFormat));
        }
    }

    /**
     * 运行时配置载入：从 DB 中最新的 recording_config 读取；失败则使用默认
     */
    private RecordingRuntimeConfig loadRuntimeConfig() {
        try {
            Map<String, Object> cfgMap = null;
            if (Files.exists(CONFIG_FILE_PATH)) {
                String json = new String(Files.readAllBytes(CONFIG_FILE_PATH), StandardCharsets.UTF_8);
                cfgMap = com.alibaba.fastjson.JSON.parseObject(json, Map.class);
            }
            String preset = getString(cfgMap, "qualityPreset", "medium");
            String format = getString(cfgMap, "format", "avi");
            int frameRate;
            float quality;
            if (cfgMap != null && cfgMap.get("frameRate") != null) {
                frameRate = ((Number) cfgMap.get("frameRate")).intValue();
            } else {
                frameRate = ("high".equalsIgnoreCase(preset)) ? 30 : ("low".equalsIgnoreCase(preset) ? 10 : 15);
            }
            if (cfgMap != null && cfgMap.get("quality") != null) {
                quality = ((Number) cfgMap.get("quality")).floatValue();
            } else {
                quality = ("high".equalsIgnoreCase(preset)) ? 1.0f : ("low".equalsIgnoreCase(preset) ? 0.5f : 0.8f);
            }
            return new RecordingRuntimeConfig(format, frameRate, quality);
        } catch (Exception e) {
            log.warn("Use default recording config due to error: {}", e.getMessage());
            return new RecordingRuntimeConfig("avi", 15, 0.8f);
        }
    }

    private String getString(Map<String, Object> map, String key, String def) {
        if (map == null) return def;
        Object v = map.get(key);
        return v == null ? def : String.valueOf(v);
    }

    private String selectExtension(String format) {
        // Monte 常用 avi，其他格式目前统一使用 avi 容器
        return ".avi";
    }

    private static class RecordingRuntimeConfig {
        final String format;
        final int frameRate;
        final float quality;
        RecordingRuntimeConfig(String format, int frameRate, float quality) {
            this.format = format;
            this.frameRate = frameRate;
            this.quality = quality;
        }
    }
}

