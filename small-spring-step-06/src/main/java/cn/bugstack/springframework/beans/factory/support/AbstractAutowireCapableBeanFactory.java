package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.PropertyValue;
import cn.bugstack.springframework.beans.PropertyValues;
import cn.bugstack.springframework.beans.factory.config.AutowireCapableBeanFactory;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.config.BeanPostProcessor;
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
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            // 调用beanDefinition的业务bean构造器 并用 cglib代理/jdk动态反射代理 来 实例化出业务bean对象
            bean = createBeanInstance(beanDefinition, beanName, args);
            // 给 Bean 填充属性 ， 对刚刚实例化好的业务bean 填充属性，
            // 若业务bean依赖了其他的业务bean，则会优先实例化其内部依赖的bean对象，然后在将业务bean的属性依次进行填充 值
            applyPropertyValues(beanName, bean, beanDefinition);
            // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
            // 此处若 传入的bean为后置处理器，说明 后置处理器在此处只是作实例化操作，并没有办法执行自身的后置处理的动作
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }
        // 将经过  1.实例化-->2.填充属性（实例化 依赖引用类,填充属性，后置处理 初始化）->3.后置处理初始化 后的业务bean放置到 singletonObjects的map容器中
        addSingleton(beanName, bean);
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructorToUse = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        for (Constructor ctor : declaredConstructors) {
            if (null != args && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    /**
     * Bean 属性填充
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {

                String name = propertyValue.getName();
                Object value = propertyValue.getValue();

                if (value instanceof BeanReference) {
                    // A 依赖 B，获取 B 的实例化
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }
                // 属性填充
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

    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 1. 执行 BeanPostProcessor Before 处理 ，依次调用所有的后置处理器执行，
        // 在将实例化好的业务bean放入到 singletonObjects的map容器 之前 进行一些初始化的动作
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 待完成内容：invokeInitMethods(beanName, wrappedBean, beanDefinition);
        invokeInitMethods(beanName, wrappedBean, beanDefinition);

        // 2. 执行 BeanPostProcessor After 处理 ，在执行完初始化方法之后，执行一些 动作
        wrappedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        // 返回经过  后置处理器的必要  处理过后 并 初始化好的 业务bean对象
        return wrappedBean;
    }
    /***
     *  初始化 方法？？？？ 做什么事情？
     */

    private void invokeInitMethods(String beanName, Object wrappedBean, BeanDefinition beanDefinition) {
        System.out.println("经过 动态代理实例化好的bean已经完成属性的装配，并且在初始化之前被后置处理器执行了一些预初始化的动作 ，现在进行方法的初始化。。。。。 ");

    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        // 刚刚调用 动态代理 实例化好并填充完属性的 bean，此时该bean尚未调用初始化方法，也 尚未放入到 singletonOjbects的map容器中
        Object result = existingBean;
        // getBeanPostProcessors()  是 该类的父类AbstractBeanFactory中的方法，用于获取 bean的所有的后置处理器
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            // 在 调用初始化方法之前  执行一些预初始化修改的操作
            Object current = processor.postProcessBeforeInitialization(result, beanName);
           // 若执行 预初始化 操作失败返回，则直接返回 进入该方法时 未经过 预初始化的bean
            if (null == current) return result;
            // 将 经过预初始化的bean作为结果bean返回
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        // 刚刚调用 动态代理 实例化好并填充完属性的 bean，此时该bean 已经调用初始化方法，在将其放入到 singletonOjbects的map容器中之前可能需要执行一些必要的 (清理)等操作
        Object result = existingBean;
        // getBeanPostProcessors()  是 该类的父类AbstractBeanFactory中的方法，用于获取 bean的所有的后置处理器
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            // 在 调用初始化方法之后  执行一些   必要的操作
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }

}
