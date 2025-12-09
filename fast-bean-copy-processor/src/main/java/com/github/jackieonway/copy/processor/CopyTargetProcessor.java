package com.github.jackieonway.copy.processor;

import com.github.jackieonway.copy.annotation.CopyTarget;
import com.github.jackieonway.copy.annotation.CopyField;
import com.github.jackieonway.copy.annotation.CopyTargetConfig;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @CopyTarget 注解的注解处理器。
 * 在编译时自动生成拷贝器类。
 */
@SupportedAnnotationTypes({
    "com.github.jackieonway.copier.annotation.CopyTarget",
    "com.github.jackieonway.copier.annotation.CopyTargetConfig"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CopyTargetProcessor extends AbstractProcessor {

    private CopierGenerator copierGenerator;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.copierGenerator = new CopierGenerator(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 处理 @CopyTarget 注解
        for (Element element : roundEnv.getElementsAnnotatedWith(CopyTarget.class)) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                try {
                    copierGenerator.generateCopier(typeElement);
                } catch (Exception e) {
                    processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "生成拷贝器失败，类型：" + typeElement.getQualifiedName() + "，错误信息：" + e.getMessage(),
                        element
                    );
                }
            }
        }

        return true;
    }
}
