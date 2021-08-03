package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;

import java.util.HashMap;
import java.util.Map;

/**
    缺省的 bean工厂实现类，里面有 beanDefinitionMap<beanName,BeanDefinition>
   该类负责 beanDefinition对象的 注册（byname），实现自BeanDefinitionRegistry接口的功能
    和beanDefinition对象的获取（by beanName），实现自（AbstractAutowireCapableBeanFactory->） AbstractBeanFactory的抽象类的方法
    BeanDefinition 其实就是一个 业务类的包装类
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    // 此处 还有一个 singletonObjects 对象 来自于其曾祖父（太爷爷）类DefaultSingletonBeanRegistry（来自于 其父类AbstractAutowireCapableBeanFactory的父类AbstractBeanFactory的父类DefaultSingletonBeanRegistry中的singletonObjects对象)
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) throw new BeansException("No bean named '" + beanName + "' is defined");
        return beanDefinition;
    }

}
