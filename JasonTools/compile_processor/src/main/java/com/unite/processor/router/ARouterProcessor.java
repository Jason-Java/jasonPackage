package com.unite.processor.router;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.unite.annotation.router.ARouter;
import com.unite.annotation.router.ARouterBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.unite.annotation.router.ARouter")
public class ARouterProcessor extends AbstractProcessor {
    private Messager messager;
    private Elements elementTools;
    private Types typeTools;
    private Filer filer;
    private String regex = "^[a-zA-Z]+(/[a-zA-Z]+)*$";

    /**
     * key 代表路由组Group
     * value 代表ARouter
     */
    private Map<String, List<ARouterBean>> cacheMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementTools = processingEnv.getElementUtils();
        typeTools = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, "开始创建路由表");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        if (elements.isEmpty()) {
            return false;
        }
        // 获取ActivityElement
        TypeElement activityTypeElement = elementTools.getTypeElement("android.app.Activity");
        for (Element el : elements) {
            // 判断此注解是否作用在Activity上面
            TypeMirror typeMirror = el.asType();
            if (!typeTools.isSubtype(typeMirror, activityTypeElement.asType())) {
                messager.printMessage(Diagnostic.Kind.ERROR, "注解@ARouter只能作用在Activity上面");
                return false;
            }
            ARouter annotation = el.getAnnotation(ARouter.class);
            String path = annotation.path();
            // 检查路由是否合乎规则
            checkPath(path);
            int index = path.indexOf('/');
            String group = path.substring(0, index);


            // 将ARouterBean 添加到缓存中
            ARouterBean bean = new ARouterBean();
            bean.setElement(el);
            bean.setPath(path);
            bean.setGroup(group);

            List<ARouterBean> aRouterBeans = cacheMap.get(group);
            if (aRouterBeans == null) {
                aRouterBeans = new ArrayList<>();
                cacheMap.put(group, aRouterBeans);
            }
            aRouterBeans.add(bean);
        }
        // ======================== 生成代码 =======================
        if (cacheMap.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "未找到路由！");
            return true;
        }
        for (Map.Entry<String, List<ARouterBean>> entry : cacheMap.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            // 生成方法  Map<String, ARouterBean> loadPath();
            // 定义方法返回类型
            TypeName returnParameter = ParameterizedTypeName.get(ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(ARouterBean.class));
            MethodSpec.Builder loadPathMeth = MethodSpec.methodBuilder("loadPath")
                    .addAnnotation(Override.class)
                    .returns(returnParameter)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("Map<String, ARouterBean> map = new $T<>()", ClassName.get(HashMap.class));
            for (ARouterBean bean : entry.getValue()) {
                loadPathMeth.addStatement("map.put($S,new ARouterBean($S,$S,$T.class))",
                        bean.getPath(),
                        bean.getPath(),
                        bean.getGroup(),
                        ClassName.get((TypeElement) bean.getElement()));
            }
            loadPathMeth.addStatement("return map");
            // 找到接口
            TypeElement interfaceTypElement = elementTools.getTypeElement("com.unite.compile_api.router.IARouterPath");

            String className = "ARouterPath$$" + entry.getKey();
            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(loadPathMeth.build())
                    .addSuperinterface(ClassName.get(interfaceTypElement))
                    .build();
            JavaFile javaFile = JavaFile.builder("com.unite.auto_generate", typeSpec)
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "路由类生成失败");
                e.printStackTrace();
            }
            messager.printMessage(Diagnostic.Kind.NOTE, entry.getKey() + "组的路由生成成功！");
        }
        return true;
    }


    /**
     * 检查Path是否合法
     *
     * @return
     */
    private void checkPath(String path) {
        if (!path.matches(regex)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "仅能包含大小写字符");
        }
    }
}