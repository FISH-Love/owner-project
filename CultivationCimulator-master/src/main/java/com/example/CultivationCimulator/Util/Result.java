package com.example.CultivationCimulator.Util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一接口返回结果封装类
 * @param <T> 数据类型
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // 状态码定义
    public static final String CODE_SUCCESS = "0";      // 成功
    public static final String CODE_ERROR = "1";       // 普通错误
    public static final String CODE_PARAM_ERROR = "1001"; // 参数错误
    public static final String CODE_UNAUTHORIZED = "401"; // 未授权
    public static final String CODE_NOT_FOUND = "404";   // 资源不存在
    public static final String CODE_SERVER_ERROR = "500"; // 服务器内部错误

    // 通用状态信息
    public static final Map<String, String> CODE_MSG_MAP = new HashMap<>();
    static {
        CODE_MSG_MAP.put(CODE_SUCCESS, "操作成功");
        CODE_MSG_MAP.put(CODE_ERROR, "操作失败");
        CODE_MSG_MAP.put(CODE_PARAM_ERROR, "参数错误");
        CODE_MSG_MAP.put(CODE_UNAUTHORIZED, "未授权访问");
        CODE_MSG_MAP.put(CODE_NOT_FOUND, "资源不存在");
        CODE_MSG_MAP.put(CODE_SERVER_ERROR, "服务器内部错误");
    }

    private String code;      // 状态码
    private String msg;       // 状态信息
    private T data;           // 返回数据
    private Map<String, Object> extra; // 扩展字段

    public Result() {
        // 默认构造
    }

    public Result(T data) {
        this.data = data;
        this.code = CODE_SUCCESS;
        this.msg = CODE_MSG_MAP.get(CODE_SUCCESS);
    }

    // 成功返回方法
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return success(data, CODE_MSG_MAP.get(CODE_SUCCESS));
    }

    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<>(data);
        result.setMsg(msg);
        return result;
    }

    // 失败返回方法
    public static <T> Result<T> error() {
        return error(CODE_ERROR);
    }

    public static <T> Result<T> error(String code) {
        return error(code, CODE_MSG_MAP.getOrDefault(code, "操作失败"));
    }

    public static <T> Result<T> error(String code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    // 参数错误
    public static <T> Result<T> paramError() {
        return error(CODE_PARAM_ERROR);
    }

    public static <T> Result<T> paramError(String msg) {
        return error(CODE_PARAM_ERROR, msg);
    }

    // 未授权
    public static <T> Result<T> unauthorized() {
        return error(CODE_UNAUTHORIZED);
    }

    // 资源不存在
    public static <T> Result<T> notFound() {
        return error(CODE_NOT_FOUND);
    }

    // 服务器错误
    public static <T> Result<T> serverError() {
        return error(CODE_SERVER_ERROR);
    }

    // 添加扩展字段
    public Result<T> addExtra(String key, Object value) {
        if (extra == null) {
            extra = new HashMap<>();
        }
        extra.put(key, value);
        return this;
    }

    public Result<T> addExtra(Map<String, Object> extraMap) {
        if (extraMap != null) {
            if (extra == null) {
                extra = new HashMap<>();
            }
            extra.putAll(extraMap);
        }
        return this;
    }

    // Getter 和 Setter
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        // 自动填充默认消息
        if (msg == null && CODE_MSG_MAP.containsKey(code)) {
            this.msg = CODE_MSG_MAP.get(code);
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", extra=" + extra +
                '}';
    }
}