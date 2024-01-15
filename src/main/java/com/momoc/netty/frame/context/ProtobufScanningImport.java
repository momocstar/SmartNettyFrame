package com.momoc.netty.frame.context;

import com.google.protobuf.GeneratedMessageV3;
import com.momoc.netty.frame.FrameMethodUtils;
import com.momoc.netty.frame.config.EnableMomocNettyFrame;
import com.momoc.netty.frame.dispatch.model.PacketMassageInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author momoc
 * @version 1.0
 * @className ProtobufScanningImport
 * @description
 * @date 2023/7/6 10:36
 */
public class ProtobufScanningImport implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    Logger log = LoggerFactory.getLogger(ProtobufScanningImport.class);



    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    // 实现EnvironmentAware接口，可以拿到系统的环境变量信息
    private Environment environment;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取启动类上的注解参数
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableMomocNettyFrame.class.getName());
        //框架没启用
        if (annotationAttributes == null || annotationAttributes.isEmpty()){
            return;
        }
        Object protoClassScanPath = annotationAttributes.get("protoClassScanPath");
        //当前启动类路径
        String className = importingClassMetadata.getClassName();
        String scanBasePackages = className.substring(0, className.lastIndexOf("."));
        if (protoClassScanPath == null){
            protoClassScanPath = new String[]{scanBasePackages};
        }
        //消息号长度
        Integer msgIdLength = environment.containsProperty("momoc.netty.msgIdLength") ? Integer.parseInt(Objects.requireNonNull(environment.getProperty("momoc.netty.msgIdLength"))) : 16 ;
        if (protoClassScanPath instanceof String[]){
            String[] scansPath = (String[]) protoClassScanPath;
            for (String path : scansPath) {
                try {
                    //获取自定义注解信息
                    List<Class<?>> candidateClasses = findCandidateClasses(path);
                    //将响应类入库
                    for (Class<?> candidateClass : candidateClasses) {
                        String name = candidateClass.getName();
                        if (name.toLowerCase().contains("vo") || name.toLowerCase().contains("dto")){
                            continue;
                        }
                        String[] arr = name.split("_");
                        if (arr.length < 2){
                            log.warn("class:{}, proto class naming does not comply with rules, [name_msgId]", name);
                            continue;
                        }
                        int msgId = Integer.parseInt(arr[1]);
                        byte[] bytes = FrameMethodUtils.fillBinaryToMsgLength(msgIdLength, msgId);
                        ProtobufHandlerMappingContext.registerResponseByteMap(msgId, bytes);
                    }
                } catch (Exception e) {
                    log.error("scanning proto file error", e);
                }
            }
        }

    }

    private String convertPath(String path) {
        return StringUtils.replace(path, ".", "/");
    }
    // 将指定包下面的符合条件的类返回
    private List<Class<?>> findCandidateClasses(String basePackage) throws IOException {
        List<Class<?>> candidates = new ArrayList<Class<?>>();
        // classpath*:com/scorpios/**/*.class
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                convertPath(basePackage) + '/' + this.DEFAULT_RESOURCE_PATTERN;
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(resourceLoader);
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);
        for (Resource resource : resources) {
            MetadataReader reader = readerFactory.getMetadataReader(resource);
            String className = reader.getClassMetadata().getClassName();
            try {
                Class<?> aClass = ClassUtils.forName(className, this.getClass().getClassLoader());
                //如果是protobuf的子类
                if (GeneratedMessageV3.class.isAssignableFrom(aClass) ){
                    candidates.add(aClass);
                }
            } catch (ClassNotFoundException e) {
                log.info("no find proto packet class {}", className);
            }
        }

        return candidates;
    }


}
