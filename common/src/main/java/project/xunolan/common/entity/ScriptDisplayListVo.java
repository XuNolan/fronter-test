package project.xunolan.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import project.xunolan.database.entity.Script;
import project.xunolan.database.repository.ScriptRepository;


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
