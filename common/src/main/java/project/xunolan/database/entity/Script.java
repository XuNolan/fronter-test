package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long usecaseId;
    String name;
    String version;
    String description;
    String data;
    Boolean isActive;  // 使用包装类型，Lombok 将生成 getIsActive() 方法
    int created;
    int updated;
}
