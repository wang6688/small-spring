package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.PropertyValue;
import cn.bugstack.springframework.beans.PropertyValues;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.config.BeanReference;
import cn.hutool.core.bean.BeanUtil;

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
            bean = createBeanInstance(beanDefinition, beanName, args);
            // 给 Bean 填充属性
            applyPropertyValues(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }
        // 将创建并实例化好的bean并且 填充过属性的bean，放到 singletonObjects 的map容器中
        addSingleton(beanName, bean);
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructorToUse = null;
        // 获得 beanDefinition 中的 业务bean的class
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 通过反射获得 业务bean 类中的所有构造器
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
    // 根据 用户选定的构造参数个数和类型 选取 业务bean对应的 构造器
        for (Constructor ctor : declaredConstructors) {
            if (null != args && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }
        // 使用 cglib代理(默认)/指定JDK动态代理 来调用上面指定的构造器来实例化 bean对象
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    /**
     * Bean 属性填充
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            // 从beanDefinitino中 获得 用户为 业务bean 指定的 所有属性信息
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            // 遍历属性信息
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                // 获取 用户为 业务bean 指定的属性名
                String name = propertyValue.getName();
                // 获取 用户为 业务bean 指定的属性值
                Object value = propertyValue.getValue();
                // 若属性值是引用类型 ，则先实例化该 引用类型的 属性对象
                if (value instanceof BeanReference) {
                    // A 依赖 B，获取 B 的实例化
                    BeanReference beanReference = (BeanReference) value;
                    // 通过用户为业务bean的属性bean 指定的beanName 来创建 引用的属性bean对象实例
                    value = getBean(beanReference.getBeanName());
                }
                // 属性填充，为业务bean的属性 填充值
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception e) {
            throw new BeansException("Error setting property values：" + beanName);
        }
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

}
