package project.xunolan.web.amisEntity.entity.usecaseVo;

import lombok.Data;
import lombok.Getter;

/*
    插入用例的信息。
 */

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
