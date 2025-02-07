package project.xunolan.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExecuteLog {
    @Id
    Long id;
    Long scriptId;
    Long recordId;
    String executeGroupId;
    int status;
    String logData;
    int isRecorded;
    int executeTime;
}
