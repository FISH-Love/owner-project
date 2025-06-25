package com.zyg.takeaway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * @Author 15827
 * @Date 2025-06-12 8:37
 */
@Slf4j  // lombok提供记录日志注解
@ServletComponentScan // 让WEB注解生效
@SpringBootApplication
@EnableTransactionManagement //开启对事物管理的支持
public class TakeoutProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakeoutProjectApplication.class,args);
        log.info("项目启动成功！！");
    }
}
