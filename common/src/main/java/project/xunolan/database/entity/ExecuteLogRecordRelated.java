package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "execute_log_record_related")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteLogRecordRelated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "execute_log_id", nullable = false)
    private Long executeLogId;

    @Column(name = "record_id", nullable = false)
    private Long recordId;
}


