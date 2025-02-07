package project.xunolan.web.amisRespVo;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class UsecaseInsertVo {
    String usecaseName;
    String usecaseDescription;
    String scriptName;
    String scriptDescription;
    String scriptVersion;
    String scriptData;
}
