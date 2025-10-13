package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 执行组与脚本关联信息实体
 */
@Entity
@Table(name = "execute_group_script_related")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExecuteGroupScriptRelated {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "`index`", nullable = false)
    private Long index;
    
    @Column(name = "script_id", nullable = false)
    private Long scriptId;
    
    @Column(name = "execute_group_id", nullable = false)
    private Long executeGroupId;
}

