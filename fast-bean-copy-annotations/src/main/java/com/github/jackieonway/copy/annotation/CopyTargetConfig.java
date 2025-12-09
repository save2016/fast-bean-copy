package com.github.jackieonway.copy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包级别 @CopyTarget 类的配置。
 * 该注解应放置在 package-info.java 文件中。
 *
 * <p>示例：
 * <pre>
 * // 在 package-info.java 中
 * @CopyTargetConfig(
 *     componentModel = ComponentModel.SPRING,
 *     nullValueStrategy = NullValueStrategy.IGNORE
 * )
 * package com.example.dto;
 * </pre>
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface CopyTargetConfig {
    /**
     * 依赖注入的组件模型。默认为 DEFAULT（静态方法）。
     */
    ComponentModel componentModel() default ComponentModel.DEFAULT;

    /**
     * Null 值处理策略。默认为 IGNORE。
     */
    NullValueStrategy nullValueStrategy() default NullValueStrategy.IGNORE;
}
