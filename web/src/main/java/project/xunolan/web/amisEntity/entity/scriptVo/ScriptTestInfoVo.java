package project.xunolan.web.amisEntity.entity.scriptVo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/*
    测试调用返回的信息。
 */
@Builder
@Data
@Accessors(chain = true)
public class ScriptTestInfoVo {
    String usecaseName;
    String usecaseDescription;
    String scriptName;
    String scriptDescription;
    String version;
    String data;
}
