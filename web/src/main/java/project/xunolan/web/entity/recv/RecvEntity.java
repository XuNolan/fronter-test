package project.xunolan.web.entity.recv;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecvEntity {
    String msgType;
    String contentType;
    String content;
}
