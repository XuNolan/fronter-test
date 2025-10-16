package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "execute_group_log_related")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteGroupLogRelated {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "execute_log_id", nullable = false)
    private Long executeLogId;

    @Column(name = "execute_term_id", nullable = false, length = 36)
    private String executeTermId;

    @Column(name = "execute_group_id", nullable = false)
    private Long executeGroupId;

    @Column(name = "created")
    private Integer created;
}


