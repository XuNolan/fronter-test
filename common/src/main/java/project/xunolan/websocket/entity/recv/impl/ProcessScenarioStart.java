package project.xunolan.websocket.entity.recv.impl;

import org.apache.tomcat.util.modeler.FeatureInfo;
import project.xunolan.websocket.entity.recv.RecvMsgBase;
import project.xunolan.karate.service.ProcessService;
import project.xunolan.websocket.utils.BeanUtils;

import javax.websocket.Session;

public class ProcessScenarioStart extends RecvMsgBase {
    //执行单位为feature。
    public String featureId;
    @Override
    public void processMsg(Session session) {
        FeatureInfo featureInfo = FeatureService.getFeatureInfoMap().get(this.featureId);
        ProcessService processService = BeanUtils.getBean(ProcessService.class);
        processService.RunFeature(session, featureInfo);
    }
}
