package project.xunolan.web.testRespVo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@Data
@Accessors(chain = true)
public class ScriptTestInfo {
    String usecaseName;
    String usecaseDescription;
    String scriptName;
    String scriptDescription;
    String version;
    String data;
}
