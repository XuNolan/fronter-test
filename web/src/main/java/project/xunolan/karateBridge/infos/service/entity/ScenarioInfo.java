package project.xunolan.karateBridge.infos.service.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


import java.io.Serializable;
import java.util.List;
//对info的增强。
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class ScenarioInfo implements Serializable {
    String scenarioId;
    String scenarioName;
    List<StepInfo> steps;
}
