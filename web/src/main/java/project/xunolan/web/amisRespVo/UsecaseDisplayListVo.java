package project.xunolan.web.amisRespVo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class UsecaseDisplayListVo {
    private String id;
    private String version;
    private String usecaseName;
    private String usecaseDescription;
    private String scriptName;
    private String scriptDescription;
    private String lastExecuteTime;
    private int status;

}
