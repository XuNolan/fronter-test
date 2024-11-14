package project.xunolan.web.entity.send.entity;

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
    String msgType;
    String content;
}
