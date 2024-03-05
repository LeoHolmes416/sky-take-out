package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类
 * 实现公共字段的自动填充
 */
@Component
@Aspect
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点 pointCut
     */
    //切入点表达式，选择包名为mapper并且方法上有AutoFill注解的方法
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 通知  使用前置通知，在执行sql语句之前填充字段
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始执行公共字段的填充...");

        //1.获取当前被拦截方法的数据库操作类型 select/insert
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); //反射获取方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); //通过签名获得方法上的注解对象
        OperationType operationType = autoFill.value(); //通过注解对象获得操作类型

        //2.获取到被拦截方法的参数（实体对象）
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){  //没有参数的特殊情况，避免出现空指针异常
            return;
        }
        Object entity = args[0];//规定第一个参数是被拦截方法的类型

        // 3.根据不同的操作类型，为实体对象的一些公共属性赋值
        LocalDateTime now = LocalDateTime.now();     //操作时间
        Long currentId = BaseContext.getCurrentId();  //操作人
        if(operationType == OperationType.INSERT){
            //插入操作，为四个公共字段赋值（创建时间，更新时间，创建人，更新人），使用反射赋值
            try {
                //获取反射方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //使用反射为获取对象赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(operationType == OperationType.UPDATE){
            //更新操作，为两个公共字段赋值
            try {
                //获取反射方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //使用反射为获取对象赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
