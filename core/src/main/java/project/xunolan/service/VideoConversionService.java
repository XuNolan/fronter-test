package project.xunolan.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * 视频格式转换服务
 * 使用FFmpeg将AVI格式转换为浏览器兼容的WebM格式
 */
@Slf4j
@Service
public class VideoConversionService {
    
    private static final String FFMPEG_COMMAND = "ffmpeg";
    private static final String CONVERTED_VIDEOS_DIR = "converted-videos";
    
    /**
     * 将AVI视频转换为WebM格式（按需转换）
     * @param aviFilePath AVI文件路径
     * @return 转换后的WebM文件路径
     */
    public CompletableFuture<String> convertAviToWebm(String aviFilePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path aviPath = Paths.get(aviFilePath);
                if (!Files.exists(aviPath)) {
                    throw new FileNotFoundException("AVI文件不存在: " + aviFilePath);
                }
                
                // 创建转换后的视频目录
                Path convertedDir = Paths.get(CONVERTED_VIDEOS_DIR);
                if (!Files.exists(convertedDir)) {
                    Files.createDirectories(convertedDir);
                }
                
                // 生成WebM文件路径
                String fileName = aviPath.getFileName().toString();
                String webmFileName = fileName.replace(".avi", ".webm");
                Path webmPath = convertedDir.resolve(webmFileName);
                
                // 构建FFmpeg命令
                ProcessBuilder pb = new ProcessBuilder(
                    FFMPEG_COMMAND,
                    "-i", aviFilePath,
                    "-c:v", "libvpx-vp8",  // VP8编码器
                    "-c:a", "libvorbis",   // Vorbis音频编码器
                    "-b:v", "1M",          // 视频比特率
                    "-b:a", "128k",        // 音频比特率
                    "-crf", "30",          // 质量参数
                    "-threads", "4",       // 使用4个线程
                    "-y",                  // 覆盖输出文件
                    webmPath.toString()
                );
                
                log.info("开始转换视频: {} -> {}", aviFilePath, webmPath.toString());
                log.info("FFmpeg命令: {}", String.join(" ", pb.command()));
                
                Process process = pb.start();
                
                // 读取FFmpeg输出
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.debug("FFmpeg: {}", line);
                    }
                }
                
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("视频转换成功: {}", webmPath.toString());
                    return webmPath.toString();
                } else {
                    log.error("视频转换失败，退出码: {}", exitCode);
                    throw new RuntimeException("视频转换失败，退出码: " + exitCode);
                }
                
            } catch (Exception e) {
                log.error("视频转换过程中发生错误", e);
                throw new RuntimeException("视频转换失败: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * 检查FFmpeg是否可用
     * @return true如果FFmpeg可用
     */
    public boolean isFFmpegAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder(FFMPEG_COMMAND, "-version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.warn("FFmpeg不可用: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取转换后的视频文件路径（如果存在）
     * @param originalAviPath 原始AVI文件路径
     * @return 转换后的WebM文件路径，如果不存在则返回null
     */
    public String getConvertedVideoPath(String originalAviPath) {
        try {
            Path aviPath = Paths.get(originalAviPath);
            String fileName = aviPath.getFileName().toString();
            String webmFileName = fileName.replace(".avi", ".webm");
            Path webmPath = Paths.get(CONVERTED_VIDEOS_DIR).resolve(webmFileName);
            
            if (Files.exists(webmPath)) {
                return webmPath.toString();
            }
            return null;
        } catch (Exception e) {
            log.warn("获取转换后视频路径失败", e);
            return null;
        }
    }
    
    /**
     * 按需转换视频（前端播放时转换）
     * @param aviFilePath AVI文件路径
     * @return 转换后的WebM文件路径
     */
    public String convertOnDemand(String aviFilePath) {
        try {
            // 检查是否已经转换过
            String existingWebmPath = getConvertedVideoPath(aviFilePath);
            if (existingWebmPath != null) {
                log.info("使用已存在的转换文件: {}", existingWebmPath);
                return existingWebmPath;
            }
            
            // 检查FFmpeg是否可用
            if (!isFFmpegAvailable()) {
                log.warn("FFmpeg不可用，无法进行按需转换");
                return null;
            }
            
            // 同步转换
            log.info("开始按需转换视频: {}", aviFilePath);
            return convertAviToWebmSync(aviFilePath);
            
        } catch (Exception e) {
            log.error("按需转换失败", e);
            return null;
        }
    }
    
    /**
     * 同步转换AVI为WebM
     * @param aviFilePath AVI文件路径
     * @return 转换后的WebM文件路径
     */
    private String convertAviToWebmSync(String aviFilePath) {
        try {
            Path aviPath = Paths.get(aviFilePath);
            log.info("检查AVI文件: {}", aviPath.toAbsolutePath());
            log.info("文件是否存在: {}", Files.exists(aviPath));
            log.info("文件大小: {}", Files.exists(aviPath) ? Files.size(aviPath) : "N/A");
            
            if (!Files.exists(aviPath)) {
                throw new FileNotFoundException("AVI文件不存在: " + aviFilePath);
            }
            
            // 创建转换后的视频目录
            Path convertedDir = Paths.get(CONVERTED_VIDEOS_DIR);
            if (!Files.exists(convertedDir)) {
                Files.createDirectories(convertedDir);
                log.info("创建转换目录: {}", convertedDir.toAbsolutePath());
            }
            
            // 生成WebM文件路径
            String fileName = aviPath.getFileName().toString();
            String webmFileName = fileName.replace(".avi", ".webm");
            Path webmPath = convertedDir.resolve(webmFileName);
            
            // 构建FFmpeg命令（使用更兼容的参数）
            ProcessBuilder pb = new ProcessBuilder(
                FFMPEG_COMMAND,
                "-i", aviFilePath,
                "-c:v", "libx264",     // 使用H.264编码器（更兼容）
                "-c:a", "aac",         // 使用AAC音频编码器
                "-b:v", "500k",        // 视频比特率
                "-b:a", "64k",         // 音频比特率
                "-preset", "fast",     // 快速编码
                "-movflags", "+faststart", // 优化网络播放
                "-y",                  // 覆盖输出文件
                webmPath.toString().replace(".webm", ".mp4") // 输出MP4格式
            );
            
            log.info("FFmpeg命令: {}", String.join(" ", pb.command()));
            
            Process process = pb.start();
            
            // 读取FFmpeg输出
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("FFmpeg: {}", line);
                    errorOutput.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                String mp4Path = webmPath.toString().replace(".webm", ".mp4");
                log.info("按需转换成功: {}", mp4Path);
                return mp4Path;
            } else {
                log.error("按需转换失败，退出码: {}", exitCode);
                log.error("FFmpeg错误输出: {}", errorOutput.toString());
                return null;
            }
            
        } catch (Exception e) {
            log.error("按需转换过程中发生错误", e);
            return null;
        }
    }
}
