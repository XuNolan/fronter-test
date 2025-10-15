package project.xunolan.websocket.entity.recv.impl;



import project.xunolan.database.entity.Script;
import project.xunolan.karate.service.FeatureStartService;
import project.xunolan.service.SessionKeyEnum;
import project.xunolan.service.ScriptService;
import project.xunolan.websocket.entity.recv.RecvMsgBase;
import project.xunolan.websocket.utils.BeanUtils;

import javax.websocket.Session;

//收到的报文实例，并不适合交给spring管理，但又需要调用service。故使用bean获取service。
public class ProcessScriptStart extends RecvMsgBase {
    //执行单位为feature。
    public Long scriptId;
    public Boolean needRecorded;  // 是否需要录制
    public Long executeGroupId;   // 可选：批量执行时传入
    public String executeTermId;  // 可选：批量执行时传入

    @Override
    public void processMsg(Session session) {
        ScriptService scriptService = BeanUtils.getBean(ScriptService.class);
        Script script = scriptService.queryScriptByScriptId(scriptId);
        FeatureStartService featureStartService = BeanUtils.getBean(FeatureStartService.class);
        // 会话中设置批量参数（不影响单次执行，单次执行不传即可）
        if (executeGroupId != null) {
            SessionKeyEnum.EXECUTE_GROUP_ID.set(session, executeGroupId);
        }
        if (executeTermId != null && !executeTermId.isEmpty()) {
            SessionKeyEnum.EXECUTE_TERM_ID.set(session, executeTermId);
        }
        // 传递录制参数给执行服务
        featureStartService.runScript(session, script, needRecorded);
    }
}
