package project.xunolan.web.entity.impl;

import project.xunolan.karateBridge.infos.entity.SendMessageType;
import project.xunolan.karateBridge.infos.entity.impl.FeatureInfo;
import project.xunolan.karateBridge.infos.utils.BeanUtils;
import project.xunolan.web.entity.RecvMsgBase;
import project.xunolan.web.service.WebSocketMessageDispatcher;

public class RequestFeature extends RecvMsgBase {

    //好像没有其他字段。

    @Override
    public void processMsg() {
        //todo：之后在发送信息时携带版本id。前端保存，发起执行请求时后端这边检查版本id是否正确，不正确时reload 提示更新再处理。
        String featureInfoContent = FeatureInfo.constructFeatureInfo(true);
        String type = SendMessageType.FeatureInfoMsg.getMsgTypeStr();
        WebSocketMessageDispatcher webSocketMessageDispatcher = BeanUtils.getBean(WebSocketMessageDispatcher.class);
        webSocketMessageDispatcher.OnSend(type, featureInfoContent);
    }
}
