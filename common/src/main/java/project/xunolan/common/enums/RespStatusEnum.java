package project.xunolan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum RespStatusEnum {
    SUCCESS(0, ""),
    FAIL(1, "操作失败"),

    ;
    private final int code;
    private final String msg;
}
