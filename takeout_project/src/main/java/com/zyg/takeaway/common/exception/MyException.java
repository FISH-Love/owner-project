package com.zyg.takeaway.common.exception;

/**
 * 自定义异常类
 */
public class MyException extends RuntimeException {
    public MyException(String message) {
        super(message);
    }
}
