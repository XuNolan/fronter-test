package project.xunolan.web.amisEntity.entity.params;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewScriptDTO {
    public String newScriptName;
    public String newScriptDescription;
    public String newScriptVersion;
    public String newScriptData;
}
