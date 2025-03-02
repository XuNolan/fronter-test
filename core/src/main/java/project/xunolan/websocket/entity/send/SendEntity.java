package project.xunolan.websocket.entity.send;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@ToString
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SendEntity implements Serializable {
    public String msgType;
    public String content;
}