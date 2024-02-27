package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }  //设置线程id

    public static Long getCurrentId() {
        return threadLocal.get();
    }  //获取线程id

    public static void removeCurrentId() {
        threadLocal.remove();
    }  //移除线程

}
