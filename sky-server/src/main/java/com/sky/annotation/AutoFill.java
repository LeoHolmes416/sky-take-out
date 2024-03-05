package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解类AutoFill
 * 用于标识某个方法需要进行功能字段自动填充处理
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)  //注解加在方法上
public @interface AutoFill {
    //指定数据库操作类型 UPDATE INSERT
    OperationType value(); //operationType 为自定义常量，内容为UPDATE INSERT
}
