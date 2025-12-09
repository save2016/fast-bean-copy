package com.github.jackieonway.copy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Null 值处理策略
 */
public enum NullValueStrategy {
    /**
     * 忽略 null 值，不更新目标字段（默认）
     */
    IGNORE,
    /**
     * 用 null 值替换，更新目标字段为 null
     */
    REPLACE
}
