package com.github.jackieonway.copy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义单个字段在 @CopyTarget 类中的映射。
 *
 * <p>示例：
 * <pre>
 * @CopyTarget(source = User.class)
 * public class UserDto {
 *     @CopyField(source = "name", target = "fullName")
 *     private String fullName;
 *
 *     @CopyField(source = "email", converter = EmailConverter.class)
 *     private String email;
 * }
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface CopyField {
    /**
     * 源字段名称。如果不指定，则使用目标字段名称。
     */
    String[] source() default {};

    /**
     * 目标字段名称。如果不指定，则使用源字段名称。
     */
    String target() default "";

    /**
     * 用于字段映射的 Java 表达式。该表达式优先于源字段映射。
     * 示例：
     * "java(source.getFirstName() + \" \" + source.getLastName())"
     */
    String expression() default "";

    /**
     * 条件表达式，用于确定是否映射该字段。
     * 示例：
     * "java(source.getName() != null && !source.getName().isEmpty())"
     */
    String condition() default "";

    /**
     * 当源字段为 null 时使用的默认值。
     */
    String defaultValue() default "";

    /**
     * 始终用于该字段的常量值，忽略源值。
     */
    String constant() default "";

    /**
     * 用于该字段的自定义类型转换器类。
     */
    Class<?> converter() default Object.class;

    /**
     * 传递给类型转换器的格式字符串。
     */
    String format() default "";
}
