package project.xunolan.web.entity.recv.impl;

import project.xunolan.web.entity.recv.RecvMsgBase;
import project.xunolan.web.entity.send.entity.impl.FeatureInfo;

import javax.websocket.Session;

public class RequestFeature extends RecvMsgBase {

    //好像没有其他字段。

    @Override
    public void processMsg() {
        //todo：之后在发送信息时携带版本id。前端保存，发起执行请求时后端这边检查版本id是否正确，不正确时reload 提示更新再处理。
        FeatureInfo.constructAndSendFeatureInfoReply(false);
    }
}
