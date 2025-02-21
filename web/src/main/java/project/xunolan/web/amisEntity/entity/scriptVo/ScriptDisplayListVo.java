package project.xunolan.web.amisEntity.entity.scriptVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import project.xunolan.database.entity.Script;

/*
   用例详情页中的脚本增删改查组件返回的信息；
 */
@Builder
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class ScriptDisplayListVo {
    private Long scriptId;
    private String scriptName;
    private String version;
    private boolean isActive;

    public static ScriptDisplayListVo fromScript(Script script) {
        return ScriptDisplayListVo.builder()
                .scriptId(script.getId())
                .scriptName(script.getName())
                .version(script.getVersion())
                .isActive(script.isActive())
                .build();
    }
}
