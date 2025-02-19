package project.xunolan.websocket.entity.recv.impl;



import project.xunolan.database.entity.Script;
import project.xunolan.karate.service.FeatureStartService;
import project.xunolan.service.ScriptService;
import project.xunolan.websocket.entity.recv.RecvMsgBase;
import project.xunolan.websocket.utils.BeanUtils;

import javax.websocket.Session;

//收到的报文实例，并不适合交给spring管理，但又需要调用service。
public class ProcessScriptStart extends RecvMsgBase {
    //执行单位为feature。
    public Long scriptId;

    @Override
    public void processMsg(Session session) {
        ScriptService scriptService = BeanUtils.getBean(ScriptService.class);
        Script script = scriptService.queryScriptByScriptId(scriptId);
        FeatureStartService featureStartService = BeanUtils.getBean(FeatureStartService.class);
        featureStartService.RunScript(session, script);
    }
}
