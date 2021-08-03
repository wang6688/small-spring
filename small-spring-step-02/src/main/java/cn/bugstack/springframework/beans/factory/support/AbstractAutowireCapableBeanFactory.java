package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;

/**
   负责 自动注入Bean的创建，实现 createBean方法的核心功能，
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    /**  该方法 实现自 AbstractBeanFactory  接口类*/
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object bean = null;
        try {
            // 通过beanDefinition中的真正业务对象的类class 来通过反射的方式创建实例对象
            bean = beanDefinition.getBeanClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeansException("Instantiation of bean failed", e);
        }
        // 调用它爷爷类中的addSingleton方法将 <beannName,beanDefinition的实例对象> 放入 单例对象的map容器中
        // 在此处 之所以能够调用到 addSingleton方法，是因为该类的父类AbstractBeanFactory的父类 DefaultSingletonBeanRegistry中有该方法的 实现逻辑。
        addSingleton(beanName, bean);
        // 返回 beanDefinition中的 业务类 通过放射方式创建处的业务类对象实例
        return bean;
    }

}
