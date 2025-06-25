package com.zyg.takeaway.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zyg.takeaway.util.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 自动填充处理类
 */
@Component // 加入到容器
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    // 在域中获取 会发生线程安全问题
    // @Autowired
    // private HttpServletRequest request;

    // 思路：在过滤器中设置线程副本 -> 在自动填充类中获取副本变量
    // 线程副本:ThreadLocal -> 保证线程安全 共享数据

    /**
     * 插入时自动填充逻辑
     *
     * @param metaObject 传递插入字段对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        log.info("自动填充.............start...insert");
        if (metaObject.hasSetter("createTime")) {
            this.fillStrategy(metaObject, "createTime", LocalDateTime.now());
        }
        if (metaObject.hasSetter("orderTime")) {
            this.fillStrategy(metaObject, "orderTime", LocalDateTime.now());
        }
        if (metaObject.hasSetter("updateTime")) {
            this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
        }
        if (metaObject.hasSetter("createUser")) {
            //
            this.fillStrategy(metaObject, "createUser", BaseContext.getCurrentId());
        }
        if (metaObject.hasSetter("updateUser")) {
            this.fillStrategy(metaObject, "updateUser", BaseContext.getCurrentId());
        }
        log.info("自动填充.............end...insert");
    }

    /**
     * 更新时自动填充逻辑
     *
     * @param metaObject 传递更新字段对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("自动填充.............start...update");
        if (metaObject.hasSetter("updateTime")) {
            this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
        }
        if (metaObject.hasSetter("updateUser")) {
            this.fillStrategy(metaObject, "updateUser", BaseContext.getCurrentId());
        }
        log.info("自动填充.............end...update");
    }
}