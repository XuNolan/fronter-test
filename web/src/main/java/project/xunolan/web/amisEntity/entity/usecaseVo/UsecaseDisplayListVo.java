package project.xunolan.web.amisEntity.entity.usecaseVo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/*
   用例的增删改查组件返回的信息；
 */
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
