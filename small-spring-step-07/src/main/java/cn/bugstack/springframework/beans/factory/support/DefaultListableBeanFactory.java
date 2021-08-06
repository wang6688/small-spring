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
    // 此处 还有个 disposableBeans 的map容器，继承来自于曾祖父类DefaultSingletonBeanRegistry（其父类AbstractAutowireCapableBeanFactory的父类AbstractBeanFactory 的父类DefaultSingletonBeanRegistry）中
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> result = new HashMap<>();
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class beanClass = beanDefinition.getBeanClass();
            // 判断beanClass 是不是 type接口的实现类
            if (type.isAssignableFrom(beanClass)) {
                 // 从beanDefinitionMap中 通过beanName 取得对应的 beanDefinition对象，
                // 并调用对应的构造器来获得 beanFactory后置处理器的代理实例对象，放入到singletonObjects的map容器中
                result.put(beanName, (T) getBean(beanName));
            }
        });
        // 返回该 type接口 对应的 实现类的代理对象
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
    public void preInstantiateSingletons() throws BeansException {
        beanDefinitionMap.keySet().forEach(this::getBean);
    }

}
