package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            // 通过 beanName 和 包装了业务bean的beanDefinition 和 显式 指定的构造参数，来调用 业务bean对应的构造方法来创建业务bean对象
            bean = createBeanInstance(beanDefinition, beanName, args);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }
        // 将通过cglib代理创建 出来的业务bean的代理对象 放置到 singletonObjects的 map容器中
        addSingleton(beanName, bean);
        // 返回该 业务bean的cglib代理对象
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructorToUse = null;
        // 获得beanDefinition中的  业务bean的class 类
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 获得业务bean的 所有构造器
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        // 遍历 业务bean的构造器，来寻找 与 用户指定的构造参数(数量)匹配的 构造器
        for (Constructor ctor : declaredConstructors) {
            // 根据 用户 显式 指定的 构造参数的个数来选定对应业务bean的构造方法
            if (null != args && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }
        // 根据选定的业务bean的构造方法 调用 cglib的实例化策略 来实例化 业务bean对象并返回
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

}
