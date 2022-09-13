package com.xmaroon.annotation_compiler;

import com.google.auto.service.AutoService;
import com.xmaroon.annotations.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * @author xuqingsong
 * 注解处理程序，用来生成代码的
 */
@AutoService(Process.class)
public class AnnotationCompiler extends AbstractProcessor {
    //1、支持的版本：固定写支持最新版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    //2、能用来处理哪些注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    //3.定义一个用来生成APT目录下面的文件的对象
    Filer filer;

    //4.初始化filer
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (set.isEmpty()) {
            return false;
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "xuqingsong---" + set);
        Map<String, List<VariableElement>> map = new HashMap<>();
        //获取APP中所有用到了BindView注解的对象
        //TypeElement 类
        //ExecutableElement 方法
        //VariableElement 属性
        Set<? extends Element> elementsAnnotationWith = roundEnv.getElementsAnnotatedWith(BindView.class);
        for (Element element : elementsAnnotationWith) {
            VariableElement variableElement = (VariableElement) element;
            String activityName = variableElement.getEnclosingElement().getSimpleName().toString();
            Class aClass = variableElement.getEnclosingElement().getClass();
            List<VariableElement> variableElementList = map.get(activityName);
            if (variableElementList == null) {
                variableElementList = new ArrayList<>();
                map.put(activityName, variableElementList);
            }
            variableElementList.add(variableElement);
        }

        //开始生成文件
        if (map.size() > 0) {
            Writer writer = null;
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String activityName = iterator.next();
                List<VariableElement> variableElements = map.get(activityName);
                //得到包名
                TypeElement enclosingElement = (TypeElement) variableElements.get(0).getEnclosingElement();
                String packageName = processingEnv.getElementUtils().getPackageOf(enclosingElement).toString();
                try {
                    JavaFileObject sourceFile = filer.createSourceFile(packageName + "." + activityName + "_ViewBinding");
                    writer = sourceFile.openWriter();
                    //        package com.example.dn_butterknife;
                    writer.write("package " + packageName + ";\n");
                    //        import com.example.dn_butterknife.IBinder;
                    writer.write("import " + packageName + ".IBinder;\n");
                    //        public class MainActivity_ViewBinding implements IBinder<
                    //        com.example.dn_butterknife.MainActivity>{
                    writer.write("public class " + activityName + "_ViewBinding implements IBinder<" +
                            packageName + "." + activityName + ">{\n");
                    //            public void bind(com.example.dn_butterknife.MainActivity target) {
                    writer.write(" @Override\n" +
                            " public void bind(" + packageName + "." + activityName + " target){");
                    //target.tvText=(android.widget.TextView)target.findViewById(2131165325);
                    for (VariableElement variableElement : variableElements) {
                        //得到名字
                        String variableName = variableElement.getSimpleName().toString();
                        //得到ID
                        int id = variableElement.getAnnotation(BindView.class).value();
                        //得到类型
                        TypeMirror typeMirror = variableElement.asType();
                        writer.write("target." + variableName + "=(" + typeMirror + ")target.findViewById(" + id + ");\n");
                    }

                    writer.write("\n}}");

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        return false;
    }
}