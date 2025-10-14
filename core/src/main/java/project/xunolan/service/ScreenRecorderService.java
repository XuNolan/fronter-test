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
     * 开始录制（新接口，不需要参数）
     * 
     * @return 录制ID
     */
    public synchronized Long startRecording() throws Exception {
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
        String fileName = String.format("test_%s", timestamp);
        
        try {
            // 获取屏幕尺寸
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();
            
            // 配置录制参数（根据配置映射）
            Format fileFormat = createFileFormat(runtimeConfig.format);
            Format screenFormat = createScreenFormat(runtimeConfig);
            Format mouseFormat = createMouseFormat();
            Format audioFormat = createAudioFormat(runtimeConfig);
            
            screenRecorder = new CustomScreenRecorder(
                    gc,
                    gc.getBounds(),
                    fileFormat,
                    screenFormat,
                    mouseFormat,
                    audioFormat,
                    new File(recordingsDir),
                    fileName + selectExtension(runtimeConfig.format)
            );
            
            screenRecorder.start();
            
            log.info("Started screen recording: {}/{}", recordingsDir, fileName + selectExtension(runtimeConfig.format));
            return System.currentTimeMillis(); // 使用时间戳作为录制ID
            
        } catch (Exception e) {
            log.error("Failed to start screen recording", e);
            throw new Exception("Failed to start screen recording: " + e.getMessage(), e);
        }
    }

    /**
     * 开始录制（旧接口，保持兼容性）
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
            Format fileFormat = createFileFormat(runtimeConfig.format);
            Format screenFormat = createScreenFormat(runtimeConfig);
            Format mouseFormat = createMouseFormat();
            Format audioFormat = createAudioFormat(runtimeConfig);
            
            screenRecorder = new CustomScreenRecorder(
                    gc,
                    gc.getBounds(),
                    fileFormat,
                    screenFormat,
                    mouseFormat,
                    audioFormat,
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
     * 停止录制（新接口，返回录制ID）
     * 
     * @param recordId 录制ID
     * @return 录像文件
     */
    public synchronized File stopRecording(Long recordId) throws Exception {
        if (screenRecorder == null) {
            log.warn("No recording in progress");
            return null;
        }
        
        try {
            screenRecorder.stop();
            
            // 获取生成的文件
            currentRecordingFile = screenRecorder.getCreatedMovieFiles().get(0);
            
            log.info("Stopped screen recording (ID: {}): {}", recordId, currentRecordingFile.getAbsolutePath());
            
            return currentRecordingFile;
            
        } catch (Exception e) {
            log.error("Failed to stop screen recording", e);
            throw new Exception("Failed to stop screen recording: " + e.getMessage(), e);
        } finally {
            screenRecorder = null;
        }
    }

    /**
     * 停止录制（旧接口，保持兼容性）
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
     * 验证配置文件是否存在且可读
     */
    public boolean isConfigFileValid() {
        try {
            if (!Files.exists(CONFIG_FILE_PATH)) {
                log.warn("Config file does not exist: {}", CONFIG_FILE_PATH);
                return false;
            }
            
            String json = new String(Files.readAllBytes(CONFIG_FILE_PATH), StandardCharsets.UTF_8);
            com.alibaba.fastjson.JSON.parseObject(json, Map.class);
            log.debug("Config file is valid: {}", CONFIG_FILE_PATH);
            return true;
        } catch (Exception e) {
            log.error("Config file is invalid: {}", e.getMessage());
            return false;
        }
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
     * 运行时配置载入：从配置文件实时读取；失败则使用默认
     */
    private RecordingRuntimeConfig loadRuntimeConfig() {
        try {
            Map<String, Object> cfgMap = null;
            if (Files.exists(CONFIG_FILE_PATH)) {
                String json = new String(Files.readAllBytes(CONFIG_FILE_PATH), StandardCharsets.UTF_8);
                cfgMap = com.alibaba.fastjson.JSON.parseObject(json, Map.class);
                log.debug("Loaded recording config from file: {}", CONFIG_FILE_PATH);
            } else {
                log.warn("Recording config file not found: {}, using default config", CONFIG_FILE_PATH);
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
            
            RecordingRuntimeConfig config = new RecordingRuntimeConfig(format, frameRate, quality);
            log.info("Recording config loaded - Format: {}, FrameRate: {}, Quality: {}, Preset: {}", 
                    config.format, config.frameRate, config.quality, preset);
            
            return config;
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
        // 根据配置选择文件扩展名
        switch (format.toLowerCase()) {
            case "mp4":
                return ".mp4";
            case "webm":
                return ".webm";
            case "avi":
            default:
                return ".avi";
        }
    }
    
    /**
     * 根据配置创建文件格式
     */
    private Format createFileFormat(String format) {
        switch (format.toLowerCase()) {
            case "mp4":
                return new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_QUICKTIME);
            case "webm":
                return new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI); // WebM 暂时用 AVI
            case "avi":
            default:
                return new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI);
        }
    }
    
    /**
     * 根据配置创建屏幕格式
     */
    private Format createScreenFormat(RecordingRuntimeConfig config) {
        // 根据质量预设选择编码器
        String encoding = selectVideoEncoding(config.format);
        
        return new Format(MediaTypeKey, MediaType.VIDEO, 
                EncodingKey, encoding,
                CompressorNameKey, encoding,
                DepthKey, 24, 
                FrameRateKey, Rational.valueOf(config.frameRate),
                QualityKey, config.quality,
                KeyFrameIntervalKey, config.frameRate * 60);
    }
    
    /**
     * 根据格式选择视频编码
     */
    private String selectVideoEncoding(String format) {
        switch (format.toLowerCase()) {
            case "mp4":
                return ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE; // MP4 暂时用 AVI 编码
            case "webm":
                return ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE; // WebM 暂时用 AVI 编码
            case "avi":
            default:
                return ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
        }
    }
    
    /**
     * 创建鼠标格式
     */
    private Format createMouseFormat() {
        return new Format(MediaTypeKey, MediaType.VIDEO, 
                EncodingKey, "black",
                FrameRateKey, Rational.valueOf(30));
    }
    
    /**
     * 根据配置创建音频格式
     */
    private Format createAudioFormat(RecordingRuntimeConfig config) {
        // 目前配置中 audio 字段未使用，暂时返回 null（无音频）
        // 后续可以根据配置中的 audio 字段决定是否录制音频
        return null;
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

