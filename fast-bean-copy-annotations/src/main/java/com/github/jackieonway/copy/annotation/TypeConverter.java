package com.github.jackieonway.copy.annotation;

/**
 * 自定义字段类型转换的转换器接口。
 * 该接口的实现可以与 @CopyField 一起使用，在拷贝期间转换字段值。
 *
 * <p>示例：
 * <pre>
 * public class StringToListConverter implements TypeConverter&lt;String, List&lt;String&gt;&gt; {
 *     @Override
 *     public List&lt;String&gt; convert(String source, String format) {
 *         if (source == null || source.isEmpty()) {
 *             return Collections.emptyList();
 *         }
 *         return Arrays.asList(source.split(format != null ? format : ","));
 *     }
 * }
 * </pre>
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 */
public interface TypeConverter<S, T> {
    /**
     * 将源对象转换为目标对象。
     *
     * @param source 源对象
     * @param format 格式字符串（可选，具体实现决定）
     * @return 目标对象
     */
    T convert(S source, String format);

    /**
     * 当 @CopyField 中未指定转换器时使用的占位符类
     */
    class None implements TypeConverter<Object, Object> {
        @Override
        public Object convert(Object source, String format) {
            return source;
        }
    }
}
