package com.github.jackieonway.copy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 依赖注入组件模型
 */
public enum ComponentModel {
    /**
     * 无依赖注入，使用静态方法
     */
    DEFAULT,
    /**
     * Spring 框架，生成 @Component 注解
     */
    SPRING,
    /**
     * CDI 框架，生成 @ApplicationScoped 注解
     */
    CDI,
    /**
     * JSR-330 标准，生成 @Named 和 @Singleton 注解
     */
    JSR330
}
