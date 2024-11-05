package project.xunolan.web.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum RecvMessageType {
    request( "request"),
    process( "process"),
    heartbeat( "heartbeat"),

    ;
    final String msgType;
}
