package project.xunolan.websocket.entity.send.impl;

import project.xunolan.karate.adapter.ScenarioInfo;
import project.xunolan.websocket.entity.send.SendMsgBase;

import java.util.ArrayList;
import java.util.List;

public class KarateFeatureInfo extends SendMsgBase {
    private List<ScenarioInfo> scenarioInfos = new ArrayList<>();
    public KarateFeatureInfo(List<ScenarioInfo> scenarioInfos){
        this.scenarioInfos.addAll(scenarioInfos);
    }
}
