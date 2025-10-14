package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "execute_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExecuteLog {
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

    @Column(name = "log_data")
    private String logData;

    @Column(name = "execute_time", nullable = false)
    private Integer executeTime;

    @Column(name = "status", nullable = false)
    private Integer status;

    /** 创建时间（UNIX 秒） */
    @Column(name = "created")
    private Integer created;
}
