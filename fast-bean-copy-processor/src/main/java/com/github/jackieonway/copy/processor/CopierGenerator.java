package com.github.jackieonway.copy.processor;

import com.github.jackieonway.copy.annotation.CopyTarget;
import com.github.jackieonway.copy.annotation.CopyField;
import com.github.jackieonway.copy.annotation.ComponentModel;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 @CopyTarget 注解的类生成拷贝器类。
 */
public class CopierGenerator {

    private final ProcessingEnvironment processingEnv;
    private final Filer filer;
    private final Messager messager;
    private final Elements elementUtils;
    private final Types typeUtils;

    public CopierGenerator(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
    }

    public void generateCopier(TypeElement targetElement) throws IOException {
        CopyTarget annotation = targetElement.getAnnotation(CopyTarget.class);
        if (annotation == null) {
            return;
        }

        String targetClassName = targetElement.getSimpleName().toString();
        String sourceClassName = extractClassName(annotation.source());
        String packageName = extractPackageName(targetElement);
        String copierClassName = targetClassName + "Copier";

        // 生成拷贝器类代码
        String copierCode = generateCopierCode(
            packageName,
            copierClassName,
            targetClassName,
            sourceClassName,
            targetElement,
            annotation
        );

        // 写入生成的文件
        JavaFileObject sourceFile = filer.createSourceFile(
            packageName + "." + copierClassName,
            targetElement
        );

        try (Writer writer = sourceFile.openWriter()) {
            writer.write(copierCode);
        }

        messager.printMessage(
            Diagnostic.Kind.NOTE,
            "已生成拷贝器类：" + packageName + "." + copierClassName
        );
    }

    private String generateCopierCode(
            String packageName,
            String copierClassName,
            String targetClassName,
            String sourceClassName,
            TypeElement targetElement,
            CopyTarget annotation) {

        StringBuilder sb = new StringBuilder();

        // 包声明
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }

        // 导入语句
        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.HashMap;\n");
        sb.append("import java.util.LinkedHashSet;\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Map;\n");
        sb.append("import java.util.Set;\n");
        sb.append("import java.util.function.UnaryOperator;\n");
        
        if (annotation.componentModel() == ComponentModel.SPRING) {
            sb.append("import org.springframework.stereotype.Component;\n");
        } else if (annotation.componentModel() == ComponentModel.CDI) {
            sb.append("import javax.enterprise.context.ApplicationScoped;\n");
        } else if (annotation.componentModel() == ComponentModel.JSR330) {
            sb.append("import javax.inject.Named;\n");
            sb.append("import javax.inject.Singleton;\n");
        }

        sb.append("\n");

        // 类声明
        sb.append("/**\n");
        sb.append(" * ").append(targetClassName).append(" 的自动生成拷贝器类\n");
        sb.append(" * 本类由 Fast Bean Copy 生成，不要手动修改。\n");
        sb.append(" */\n");

        if (annotation.componentModel() == ComponentModel.SPRING) {
            sb.append("@Component\n");
        } else if (annotation.componentModel() == ComponentModel.CDI) {
            sb.append("@ApplicationScoped\n");
        } else if (annotation.componentModel() == ComponentModel.JSR330) {
            sb.append("@Named\n");
            sb.append("@Singleton\n");
        }

        if (annotation.componentModel() == ComponentModel.DEFAULT) {
            sb.append("public final class ").append(copierClassName).append(" {\n");
            sb.append("    private ").append(copierClassName).append("() {\n");
            sb.append("        // 工具类\n");
            sb.append("    }\n\n");
        } else {
            sb.append("public class ").append(copierClassName).append(" {\n\n");
        }

        // 获取忽略的字段
        Set<String> ignoreFields = new HashSet<>(Arrays.asList(annotation.ignore()));

        // 生成 toDto 方法
        generateToMethod(sb, annotation.componentModel(), targetClassName, sourceClassName, "toDto", ignoreFields);

        // 生成 fromDto 方法
        generateFromMethod(sb, annotation.componentModel(), sourceClassName, targetClassName, "fromDto", ignoreFields);

        // 生成 List 批量转换方法
        generateListMethods(sb, annotation.componentModel(), targetClassName, sourceClassName, ignoreFields);

        // 生成 Set 批量转换方法
        generateSetMethods(sb, annotation.componentModel(), targetClassName, sourceClassName, ignoreFields);

        // 生成 Map 批量转换方法
        generateMapMethods(sb, annotation.componentModel(), targetClassName, sourceClassName, ignoreFields);

        // 生成更新方法
        generateUpdateMethods(sb, annotation.componentModel(), targetClassName, sourceClassName, ignoreFields);

        sb.append("}\n");

        return sb.toString();
    }

    private void generateToMethod(
            StringBuilder sb,
            ComponentModel componentModel,
            String targetClassName,
            String sourceClassName,
            String methodName,
            Set<String> ignoreFields) {

        String staticModifier = componentModel == ComponentModel.DEFAULT ? "static " : "";

        sb.append("    /**\n");
        sb.append("     * 将 ").append(sourceClassName).append(" 转换为 ").append(targetClassName).append("\n");
        sb.append("     * @param source 源对象\n");
        sb.append("     * @return 目标对象\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append(targetClassName).append(" ").append(methodName)
            .append("(").append(sourceClassName).append(" source) {\n");
        sb.append("        if (source == null) return null;\n");
        sb.append("        ").append(targetClassName).append(" target = new ").append(targetClassName).append("();\n");
        sb.append("        // 字段映射在这里进行\n");
        sb.append("        return target;\n");
        sb.append("    }\n\n");

        // 带自定义处理的重载方法
        sb.append("    /**\n");
        sb.append("     * 将 ").append(sourceClassName).append(" 转换为 ").append(targetClassName)
            .append("，并进行自定义处理\n");
        sb.append("     * @param source 源对象\n");
        sb.append("     * @param customizer 自定义处理器\n");
        sb.append("     * @return 目标对象\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append(targetClassName).append(" ").append(methodName)
            .append("(").append(sourceClassName).append(" source, UnaryOperator<").append(targetClassName)
            .append("> customizer) {\n");
        sb.append("        ").append(targetClassName).append(" target = ").append(methodName).append("(source);\n");
        sb.append("        return customizer.apply(target);\n");
        sb.append("    }\n\n");
    }

    private void generateFromMethod(
            StringBuilder sb,
            ComponentModel componentModel,
            String sourceClassName,
            String targetClassName,
            String methodName,
            Set<String> ignoreFields) {

        String staticModifier = componentModel == ComponentModel.DEFAULT ? "static " : "";

        sb.append("    /**\n");
        sb.append("     * 将 ").append(targetClassName).append(" 转换为 ").append(sourceClassName).append("\n");
        sb.append("     * @param source 源对象\n");
        sb.append("     * @return 目标对象\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append(sourceClassName).append(" ").append(methodName)
            .append("(").append(targetClassName).append(" source) {\n");
        sb.append("        if (source == null) return null;\n");
        sb.append("        ").append(sourceClassName).append(" target = new ").append(sourceClassName).append("();\n");
        sb.append("        // 字段映射在这里进行\n");
        sb.append("        return target;\n");
        sb.append("    }\n\n");

        // 带自定义处理的重载方法
        sb.append("    /**\n");
        sb.append("     * 将 ").append(targetClassName).append(" 转换为 ").append(sourceClassName)
            .append("，并进行自定义处理\n");
        sb.append("     * @param source 源对象\n");
        sb.append("     * @param customizer 自定义处理器\n");
        sb.append("     * @return 目标对象\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append(sourceClassName).append(" ").append(methodName)
            .append("(").append(targetClassName).append(" source, UnaryOperator<").append(sourceClassName)
            .append("> customizer) {\n");
        sb.append("        ").append(sourceClassName).append(" target = ").append(methodName).append("(source);\n");
        sb.append("        return customizer.apply(target);\n");
        sb.append("    }\n\n");
    }

    private void generateListMethods(
            StringBuilder sb,
            ComponentModel componentModel,
            String targetClassName,
            String sourceClassName,
            Set<String> ignoreFields) {

        String staticModifier = componentModel == ComponentModel.DEFAULT ? "static " : "";

        sb.append("    /**\n");
        sb.append("     * 将 ").append(sourceClassName).append(" 列表转换为 ").append(targetClassName).append(" 列表\n");
        sb.append("     * @param sources 源列表\n");
        sb.append("     * @return 目标列表\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append("List<").append(targetClassName).append("> to")
            .append(targetClassName).append("List(List<").append(sourceClassName).append("> sources) {\n");
        sb.append("        if (sources == null) return null;\n");
        sb.append("        List<").append(targetClassName).append("> result = new ArrayList<>(sources.size());\n");
        sb.append("        for (").append(sourceClassName).append(" source : sources) {\n");
        sb.append("            result.add(toDto(source));\n");
        sb.append("        }\n");
        sb.append("        return result;\n");
        sb.append("    }\n\n");

        sb.append("    /**\n");
        sb.append("     * 将 ").append(targetClassName).append(" 列表转换为 ").append(sourceClassName).append(" 列表\n");
        sb.append("     * @param sources 源列表\n");
        sb.append("     * @return 目标列表\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append("List<").append(sourceClassName).append("> from")
            .append(targetClassName).append("List(List<").append(targetClassName).append("> sources) {\n");
        sb.append("        if (sources == null) return null;\n");
        sb.append("        List<").append(sourceClassName).append("> result = new ArrayList<>(sources.size());\n");
        sb.append("        for (").append(targetClassName).append(" source : sources) {\n");
        sb.append("            result.add(fromDto(source));\n");
        sb.append("        }\n");
        sb.append("        return result;\n");
        sb.append("    }\n\n");
    }

    private void generateSetMethods(
            StringBuilder sb,
            ComponentModel componentModel,
            String targetClassName,
            String sourceClassName,
            Set<String> ignoreFields) {

        String staticModifier = componentModel == ComponentModel.DEFAULT ? "static " : "";

        sb.append("    /**\n");
        sb.append("     * 将 ").append(sourceClassName).append(" 集合转换为 ").append(targetClassName).append(" 集合\n");
        sb.append("     * @param sources 源集合\n");
        sb.append("     * @return 目标集合\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append("Set<").append(targetClassName).append("> to")
            .append(targetClassName).append("Set(Set<").append(sourceClassName).append("> sources) {\n");
        sb.append("        if (sources == null) return null;\n");
        sb.append("        Set<").append(targetClassName).append("> result = new LinkedHashSet<>(sources.size());\n");
        sb.append("        for (").append(sourceClassName).append(" source : sources) {\n");
        sb.append("            result.add(toDto(source));\n");
        sb.append("        }\n");
        sb.append("        return result;\n");
        sb.append("    }\n\n");

        sb.append("    /**\n");
        sb.append("     * 将 ").append(targetClassName).append(" 集合转换为 ").append(sourceClassName).append(" 集合\n");
        sb.append("     * @param sources 源集合\n");
        sb.append("     * @return 目标集合\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append("Set<").append(sourceClassName).append("> from")
            .append(targetClassName).append("Set(Set<").append(targetClassName).append("> sources) {\n");
        sb.append("        if (sources == null) return null;\n");
        sb.append("        Set<").append(sourceClassName).append("> result = new LinkedHashSet<>(sources.size());\n");
        sb.append("        for (").append(targetClassName).append(" source : sources) {\n");
        sb.append("            result.add(fromDto(source));\n");
        sb.append("        }\n");
        sb.append("        return result;\n");
        sb.append("    }\n\n");
    }

    private void generateMapMethods(
            StringBuilder sb,
            ComponentModel componentModel,
            String targetClassName,
            String sourceClassName,
            Set<String> ignoreFields) {

        String staticModifier = componentModel == ComponentModel.DEFAULT ? "static " : "";

        sb.append("    /**\n");
        sb.append("     * 将 ").append(sourceClassName).append(" 的 Map 值转换为 ").append(targetClassName).append("\n");
        sb.append("     * @param sources 源 Map\n");
        sb.append("     * @return 目标 Map\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append("<K> Map<K, ").append(targetClassName).append("> to")
            .append(targetClassName).append("Map(Map<K, ").append(sourceClassName).append("> sources) {\n");
        sb.append("        if (sources == null) return null;\n");
        sb.append("        Map<K, ").append(targetClassName).append("> result = new HashMap<>(sources.size());\n");
        sb.append("        sources.forEach((key, value) -> result.put(key, toDto(value)));\n");
        sb.append("        return result;\n");
        sb.append("    }\n\n");

        sb.append("    /**\n");
        sb.append("     * 将 ").append(targetClassName).append(" 的 Map 值转换为 ").append(sourceClassName).append("\n");
        sb.append("     * @param sources 源 Map\n");
        sb.append("     * @return 目标 Map\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append("<K> Map<K, ").append(sourceClassName).append("> from")
            .append(targetClassName).append("Map(Map<K, ").append(targetClassName).append("> sources) {\n");
        sb.append("        if (sources == null) return null;\n");
        sb.append("        Map<K, ").append(sourceClassName).append("> result = new HashMap<>(sources.size());\n");
        sb.append("        sources.forEach((key, value) -> result.put(key, fromDto(value)));\n");
        sb.append("        return result;\n");
        sb.append("    }\n\n");
    }

    private void generateUpdateMethods(
            StringBuilder sb,
            ComponentModel componentModel,
            String targetClassName,
            String sourceClassName,
            Set<String> ignoreFields) {

        String staticModifier = componentModel == ComponentModel.DEFAULT ? "static " : "";

        sb.append("    /**\n");
        sb.append("     * 用 ").append(sourceClassName).append(" 更新 ").append(targetClassName).append("\n");
        sb.append("     * @param target 要更新的目标对象\n");
        sb.append("     * @param source 源对象\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append("void update").append(targetClassName)
            .append("(").append(targetClassName).append(" target, ").append(sourceClassName).append(" source) {\n");
        sb.append("        if (target == null || source == null) return;\n");
        sb.append("        // 字段更新在这里进行\n");
        sb.append("    }\n\n");

        sb.append("    /**\n");
        sb.append("     * 用 ").append(targetClassName).append(" 更新 ").append(sourceClassName).append("\n");
        sb.append("     * @param target 要更新的目标对象\n");
        sb.append("     * @param source 源对象\n");
        sb.append("     */\n");
        sb.append("    public ").append(staticModifier).append("void update").append(sourceClassName)
            .append("(").append(sourceClassName).append(" target, ").append(targetClassName).append(" source) {\n");
        sb.append("        if (target == null || source == null) return;\n");
        sb.append("        // 字段更新在这里进行\n");
        sb.append("    }\n");
    }

    private String extractClassName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    private String extractPackageName(TypeElement element) {
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }
}
