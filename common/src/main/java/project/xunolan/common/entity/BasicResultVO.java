package project.xunolan.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import project.xunolan.common.enums.RespStatusEnum;

@Getter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public final class BasicResultVO<T> {

    private int status;

    private String msg;

    private T data;

    public BasicResultVO(RespStatusEnum status) {
        this(status, null);
    }

    public BasicResultVO(RespStatusEnum status, T data) {
        this(status, status.getMsg(), data);
    }

    public BasicResultVO(RespStatusEnum status, String msg, T data) {
        this.status = status.getCode();
        this.msg = msg;
        this.data = data;
    }

    public static BasicResultVO<Void> success() {
        return new BasicResultVO<>(RespStatusEnum.SUCCESS);
    }

    public static <T> BasicResultVO<T> success(String msg) {
        return new BasicResultVO<>(RespStatusEnum.SUCCESS, msg, null);
    }

    public static <T> BasicResultVO<T> success(T data) {
        return new BasicResultVO<>(RespStatusEnum.SUCCESS, data);
    }

    public static <T> BasicResultVO<T> fail() {
        return new BasicResultVO<>(
                RespStatusEnum.FAIL,
                RespStatusEnum.FAIL.getMsg(),
                null
        );
    }

    public static <T> BasicResultVO<T> fail(String msg) {
        return fail(RespStatusEnum.FAIL, msg);
    }

    public static <T> BasicResultVO<T> fail(RespStatusEnum status) {
        return fail(status, status.getMsg());
    }

    public static <T> BasicResultVO<T> fail(RespStatusEnum status, String msg) {
        return new BasicResultVO<>(status, msg, null);
    }

}
