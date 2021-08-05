package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.ConfigurableListableBeanFactory;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    // 此处还有个 instantiationStrategy 的代理策略对象（cglib/jdk动态反射代理) 继承于其父类 AbstractAutowireCapableBeanFactory中
    // 此处还有个 beanPostProcessors 对象 继承来自于其爷爷类AbstractBeanFactory（其父类AbstractAutowireCapableBeanFactory中的父类AbstractBeanFactory）中
    // 此处还有个 singletonObjects 的map容器 继承来自于 曾祖父类DefaultSingletonBeanRegistry（其父类AbstractAutowireCapableBeanFactory的父类AbstractBeanFactory 的父类DefaultSingletonBeanRegistry）中
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    /* 此方法用于查找type接口的实现类，并对其实现类进行实例化填充对象等一些列操作
    * 目前type接口主要是 1. BeanFactoryPostProcessor  2. BeanPostProcessor
    * **/
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        // 存储 type接口的实现类有哪些
        Map<String, T> result = new HashMap<>();
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class beanClass = beanDefinition.getBeanClass();
            // 判断beanDefinition 是否是 其type 接口的实现类
            if (type.isAssignableFrom(beanClass)) {
                // 若 beanDefinition 是 type接口的实现类，则将beanName，此时通过beanName使用beanDefinition的构造器调用动态代理实例化对象->填充属性->作后置处理器的操作
                result.put(beanName, (T) getBean(beanName));
            }
        });
        return result;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) throw new BeansException("No bean named '" + beanName + "' is defined");
        return beanDefinition;
    }

    @Override
    // 依次将beanDefinitionMap中的所有 beanDefinition 对象获取其构造器，调用动态代理实例化对象，填充属性，
    // 对实例化完且填充好属性的业务对象bean 调用后置处理器 进行一些 初始化操作，最后将其 都放入到 singletonObjects的map容器中
    public void preInstantiateSingletons() throws BeansException {
        beanDefinitionMap.keySet().forEach(this::getBean);
    }

}
