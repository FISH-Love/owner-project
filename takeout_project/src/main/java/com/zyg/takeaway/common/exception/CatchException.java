package com.zyg.takeaway.common.exception;


import com.zyg.takeaway.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 处理异常类
 */
@Slf4j
@RestControllerAdvice
public class CatchException {

    /**
     * 捕获处理自定义异常
     *
     * @param e 异常类型
     * @return 返回异常处理结果
     */
    @ExceptionHandler(MyException.class)
    public R<String> catchMyException(MyException e) {
        // 异常处理
        // ①  保存现场（记录 日志）
        log.error("异常了:{}", e.getMessage());
        // ②  通知运维或者技术支持
        System.out.println("通知运维.....MyException");
        // ③  给用户友好提示
        return R.error("您的网络有问题，请拔掉网线重试一下！");
    }

    /**
     * 其他异常
     *
     * @param e 异常类型
     * @return 返回异常处理结果
     */
    @ExceptionHandler(Exception.class)
    public R<String> catchException(Exception e) {
        // 异常处理
        // ①  保存现场（记录 日志）
        log.error("异常了:{}", e.getMessage());
        // ②  通知运维或者技术支持
        System.out.println("通知运维.....Exception");
        // ③  给用户友好提示
        return R.error("您的网络有问题，请拔掉网线重试一下！");
    }
}