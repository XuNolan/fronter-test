package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Blob;

/**
 * 录像信息实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "record_info")
public class RecordInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;
    
    @Column(name = "log_id", nullable = false)
    private Long logId;
    
    /**
     * 录像数据（存储rrweb events的JSON字符串）
     * 或者视频文件的二进制数据
     */
    @Lob
    @Column(name = "record_data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] recordData;
    
    /**
     * 录像类型：rrweb, video, http_log
     */
    @Column(name = "record_type", length = 20)
    private String recordType;
    
    /**
     * 录像元数据（JSON格式）：
     * - duration: 录制时长（毫秒）
     * - size: 数据大小（字节）
     * - start_time: 开始时间
     * - end_time: 结束时间
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
}

