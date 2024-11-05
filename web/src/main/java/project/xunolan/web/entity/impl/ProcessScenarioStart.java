package project.xunolan.web.entity.impl;

import com.intuit.karate.core.Feature;
import project.xunolan.karateBridge.actions.service.ProcessService;
import project.xunolan.karateBridge.infos.entity.impl.FeatureInfo;
import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.karateBridge.infos.utils.BeanUtils;
import project.xunolan.web.entity.RecvMsgBase;

public class ProcessScenarioStart extends RecvMsgBase {
    //执行单位为feature。
    String featureId;
    @Override
    public void processMsg() {
        FeatureInfo featureInfo = FeatureService.getFeatureInfoMap().get(this.featureId);
        ProcessService processService = BeanUtils.getBean(ProcessService.class);
        processService.RunFeature(featureInfo);
    }
}
