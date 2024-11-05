package project.xunolan.karateBridge.infos.entity.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class ScenarioInfo implements Serializable {
    String ScenarioId;
    String scenarioName;
    List<StepInfo> steps = new ArrayList<>();
}
