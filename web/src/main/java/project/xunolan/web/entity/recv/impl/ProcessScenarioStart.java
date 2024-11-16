package project.xunolan.web.entity.recv.impl;

import project.xunolan.karateBridge.actions.service.ProcessService;
import project.xunolan.karateBridge.infos.service.entity.FeatureInfo;
import project.xunolan.karateBridge.infos.service.FeatureService;
import project.xunolan.web.utils.BeanUtils;
import project.xunolan.web.entity.recv.RecvMsgBase;

import javax.websocket.Session;

public class ProcessScenarioStart extends RecvMsgBase {
    //执行单位为feature。
    String featureId;
    @Override
    public void processMsg(Session session) {
        FeatureInfo featureInfo = FeatureService.getFeatureInfoMap().get(this.featureId);
        ProcessService processService = BeanUtils.getBean(ProcessService.class);
        processService.RunFeature(session, featureInfo);
    }
}
