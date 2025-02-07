package project.xunolan.database.repository.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Access;

@Builder
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class DisplayListVo {
    private String id;
    private String version;
    private String usecaseName;
    private String usecaseDescription;
    private String scriptName;
    private String scriptDescription;
    private String lastExecuteTime;
    private int status;

}
