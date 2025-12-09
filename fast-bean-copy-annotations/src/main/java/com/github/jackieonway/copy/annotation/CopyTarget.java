package com.github.jackieonway.copy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记为拷贝目标。带有该注解的类将用于生成一个拷贝器类，
 * 可以将对象从源类型转换为该类型。
 *
 * <p>示例：
 * <pre>
 * @CopyTarget(source = User.class)
 * public class UserDto {
 *     private Long id;
 *     private String name;
 *     private String email;
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CopyTarget {
    /**
     * 源类类型，用于拷贝。该字段为必填项。
     */
    Class<?> source();

    /**
     * 拷贝时忽略的字段名称列表。列表中的字段不会被映射。
     */
    String[] ignore() default {};

    /**
     * 用于字段转换的自定义类型转换器类。
     */
    Class<?>[] uses() default {};

    /**
     * 依赖注入的组件模型。默认为 DEFAULT（静态方法）。
     */
    ComponentModel componentModel() default ComponentModel.DEFAULT;

    /**
     * 映射前要调用的方法名称。该方法应该接受源对象作为参数
     * 并且返回类型为 void。
     */
    String beforeMapping() default "";

    /**
     * Null 值处理策略。默认为 IGNORE。
     */
    NullValueStrategy nullValueStrategy() default NullValueStrategy.IGNORE;
}
