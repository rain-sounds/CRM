package cn.cordys.common.response.result;

import lombok.Data;

/**
 * 统一响应结果封装类（泛型版本）
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /**
     * 状态码，默认 200 成功
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public Result() {
        this.code = CrmHttpResultCode.SUCCESS.getCode();
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>();
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(CrmHttpResultCode.SUCCESS.getCode(), message);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(CrmHttpResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 错误响应（带消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(CrmHttpResultCode.FAILED.getCode(), message);
    }

    /**
     * 错误响应（带状态码和消息）
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 错误响应（带消息和数据）
     */
    public static <T> Result<T> error(String message, T data) {
        return new Result<>(CrmHttpResultCode.FAILED.getCode(), message, data);
    }

    /**
     * 自定义状态码响应
     */
    public static <T> Result<T> result(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return CrmHttpResultCode.SUCCESS.getCode() == this.code;
    }
}
