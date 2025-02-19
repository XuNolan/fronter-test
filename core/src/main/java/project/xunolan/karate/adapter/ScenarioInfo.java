package project.xunolan.karate.adapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Accessors(fluent = true)
public class ScenarioInfo {
    public int index; //Scenario index
    public String scenarioName;
    public List<StepInfo> stepInfos;
}
