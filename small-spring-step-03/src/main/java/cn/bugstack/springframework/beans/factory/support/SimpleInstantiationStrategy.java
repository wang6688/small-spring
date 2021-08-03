package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 简单标准的实例化bean的 策略（采用JDK动态代理的 clazz.getDeclaredConstructor()方法来反射出实例化对象 ）
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {

    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args) throws BeansException {
        // 获得 BeanDefinition中的 业务bean的class
        Class clazz = beanDefinition.getBeanClass();
        System.err.println("简单的实例化bean的 策略 被调用");
        try {
            // 若用户指定了构造器，则选用 用户指定的构造器来反射出实例化对象
            if (null != ctor) {
                return clazz.getDeclaredConstructor(ctor.getParameterTypes()).newInstance(args);
            } else {
                // 否则 选用 缺省的 无参构造器来反射出 业务bean的实例化对象
                return clazz.getDeclaredConstructor().newInstance();
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeansException("Failed to instantiate [" + clazz.getName() + "]", e);
        }
    }

}
