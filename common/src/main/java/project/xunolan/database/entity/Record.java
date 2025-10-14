package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "record")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "usecase_id", nullable = false)
    private Long usecaseId;

    @Column(name = "script_id", nullable = false)
    private Long scriptId;

    @Column(name = "execute_group_id")
    private Long executeGroupId;

    /** 执行状态：0成功 1失败 2中止/进行中 */
    @Column(name = "status", nullable = false)
    private Integer status;

    /** 存储方式：local 或 database（db） */
    @Column(name = "storage_type", nullable = false)
    private String storageType;

    /** local 模式下文件路径 */
    @Column(name = "record_url")
    private String recordUrl;

    /** db 模式下二进制内容 */
    @Lob
    @Column(name = "record_data")
    private byte[] recordData;

    /** 录像元数据（JSON） */
    @Column(name = "metadata")
    private String metadata;

    /** 录制质量类型：0低 1中 2高 */
    @Column(name = "record_config_type", nullable = false)
    private Integer recordConfigType;

    /** 执行时长（毫秒） */
    @Column(name = "execute_time", nullable = false)
    private Integer executeTime;
}


