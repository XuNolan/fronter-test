package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 执行组信息实体
 */
@Entity
@Table(name = "execute_group_id")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExecuteGroupId {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "group_name", nullable = false, length = 32)
    private String groupName;
    
    @Column(name = "created", nullable = false)
    private Integer created;
    
    @Column(name = "updated", nullable = false)
    private Integer updated;
}

