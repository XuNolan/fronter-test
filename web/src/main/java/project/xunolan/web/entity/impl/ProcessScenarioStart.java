package project.xunolan.web.entity.impl;

import com.intuit.karate.core.Feature;
import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.web.entity.RecvMsgBase;

public class ProcessScenarioStart extends RecvMsgBase {
    //执行单位为feature。
    String featureId;
    @Override
    public void processMsg() {
        Feature feature = FeatureService.getFeatureInfoMap().get(this.featureId).getFeature();

    }
}
