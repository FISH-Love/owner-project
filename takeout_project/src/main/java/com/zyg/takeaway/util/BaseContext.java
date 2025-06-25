package com.zyg.takeaway.util;

/**
 * 基于ThreadLocal封装工具类
 * <p>
 * 用户保存和获取当前登录用户id
 */
public class BaseContext {
    /**
     * 设置一个静态不可修改的线程副本
     */
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 获取线程副本值
     *
     * @param id 获取id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 设置线程副本值
     *
     * @return 传输id
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}