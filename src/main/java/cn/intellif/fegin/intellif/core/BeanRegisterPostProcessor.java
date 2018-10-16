package cn.intellif.fegin.intellif.core;

import cn.intellif.fegin.intellif.annotation.EnableFegin;
import cn.intellif.fegin.intellif.annotation.FeginClient;
import cn.intellif.fegin.intellif.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class BeanRegisterPostProcessor implements ImportBeanDefinitionRegistrar,ResourceLoaderAware,EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(EnableFegin.class.getCanonicalName());
        if(annotationAttributes==null)
            return;
        ClassPathScanningCandidateComponentProvider provider = getScanner();
        provider.setResourceLoader(resourceLoader);
        Set<String> basePackages = new LinkedHashSet<>();
        for(String pkg:(String[])annotationAttributes.get("basePackages")){
            if(StringUtils.hasText(pkg)){
                basePackages.add(pkg);
            }
        }

        if(basePackages.size()==0){
            basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }

        for(String basePackage:basePackages){

            Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(basePackage);
            if(beanDefinitions!=null){
                for(BeanDefinition beanDefinition:beanDefinitions){
                    logger.info(Constant.PRE_LOG+"动态注册："+beanDefinition.getBeanClassName());
                    registerBeanDefination(beanDefinition,registry,annotationAttributes);
                }
            }
        }

    }

    private void registerBeanDefination(BeanDefinition beanDefinition,BeanDefinitionRegistry registry,Map<String,Object> annotationAttributes){
        if(beanDefinition instanceof GenericBeanDefinition){
            GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) beanDefinition;
            //这里只能调用上面的getBeanClassName调用getBeanClass会报错
            String className = genericBeanDefinition.getBeanClassName();
            genericBeanDefinition.setBeanClass(FeginInvokeFactoryBean.class);
            //及时类上面成员变量为类对象使用类全限名也是可以的
            genericBeanDefinition.getPropertyValues().add("clazz",className);
            genericBeanDefinition.getPropertyValues().add("fallback",annotationAttributes.get("fallback"));
            genericBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
            registry.registerBeanDefinition(className,genericBeanDefinition);
        }
    }

    private ClassPathScanningCandidateComponentProvider getScanner(){
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().hasAnnotation(FeginClient.class.getCanonicalName())&&beanDefinition.getMetadata().isInterface()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }

            protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
                return true;
            }
        };


    }
}
