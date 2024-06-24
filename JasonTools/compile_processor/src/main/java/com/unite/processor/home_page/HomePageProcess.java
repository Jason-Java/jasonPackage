package com.unite.processor.home_page;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.unite.annotation.home_page.ActionModelAnnotation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

// 标明此类是注解处理器
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
// 需要处理的注解
@SupportedAnnotationTypes("com.unite.annotation.home_page.ActionModelAnnotation")
public class HomePageProcess extends AbstractProcessor {

    private Elements elementsTools;
    private Messager messager;
    private Types typesTools;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementsTools = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        typesTools = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, "环境设置成功");


    }

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(ActionModelAnnotation.class);
        if (elementsAnnotatedWith.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "处理器集合已经为空啦");
            return true;
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "正在处理集合数据");
        ClassName className = ClassName.get("com.unite.auto_config", "ConfigMange");
        // 单例类属性
        FieldSpec instanceFieldSpec = FieldSpec.builder(className, "instance")
                .addModifiers(Modifier.PRIVATE)
                .addModifiers(Modifier.VOLATILE)
                .addModifiers(Modifier.STATIC)
                .build();

        // 单例方法
        MethodSpec getInstanceMethod = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(className)
                .beginControlFlow("if (instance == null)")
                .beginControlFlow("synchronized ($T.class)", className)
                .beginControlFlow("if (instance == null)")
                .addStatement("instance = new $T()", className)
                .endControlFlow()
                .endControlFlow()
                .endControlFlow()
                .addStatement("return instance")
                .build();


        // 获取IActionConfig 接口
        TypeElement actionConfigElement = elementsTools.getTypeElement("com.unite.compile_api.home_page.IActionConfig");
        // 生成 Map<String, Class<? extends IActionConfig>>
        TypeName typeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(actionConfigElement)))
        );
        // 类属性
        FieldSpec configMapFieldSpec = FieldSpec.builder(typeName, "configMap", Modifier.PRIVATE).build();
        // 生成 构造函数
        MethodSpec.Builder constructorMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("configMap = new $T<>()", ClassName.get(HashMap.class));
        // 给HasMap填充数据

        for (Element element : elementsAnnotatedWith) {
            // 获取注解的值
            ActionModelAnnotation annotation = element.getAnnotation(ActionModelAnnotation.class);
            String value = annotation.value();
            constructorMethod.addStatement("configMap.put($S,$T.class)",  value, ClassName.get((TypeElement) element));
        }


        // 定义方法的返回类型
        TypeName loadActionConfigReturnTypeName = ParameterizedTypeName.get(ClassName.get(Class.class),
                WildcardTypeName.subtypeOf(ClassName.get(actionConfigElement)));
        // 定义方法的参数
        ParameterSpec modelParameter = ParameterSpec.builder(ClassName.get(String.class), "model").build();
        MethodSpec loadActionConfigMethod = MethodSpec.methodBuilder("loadActionConfig")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(actionConfigElement))
                .addParameter(modelParameter)
                .addStatement("Class<? extends IActionConfig> aClass = configMap.get(model)")
                .beginControlFlow("try")
                .addStatement("return aClass.newInstance()")
                .endControlFlow()
                .beginControlFlow("catch ($T e)", ClassName.get(Exception.class))
                .addStatement("throw new $T($S)", ClassName.get(RuntimeException.class), "未发现配置类")
                .endControlFlow()
                .build();


        // 定义类
        TypeSpec typeSpec = TypeSpec.classBuilder("ConfigMange")
                .addModifiers(Modifier.PUBLIC)
                .addField(instanceFieldSpec)
                .addMethod(getInstanceMethod)
                .addField(configMapFieldSpec)
                .addMethod(constructorMethod.build())
                .addMethod(loadActionConfigMethod)
                .build();


        JavaFile javaFile = JavaFile.builder("com.unite.auto_config", typeSpec).build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "生成类失败");
            e.printStackTrace();
        }
        return true;
    }
}