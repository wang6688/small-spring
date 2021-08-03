package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.BeanFactory;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;

/**
  抽象的Bean工厂类，声明管理： 将<beanName,beanDefinition>  创建方法的定义，和通过beanName 获得BeanDefinition的方法的定义；
  实现方法 ： 通过className  获得单例对象的方法 ，来自于
 * BeanDefinition 注册表接口
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

    @Override
    public Object getBean(String name) throws BeansException {
        // 通过beanName 获得 单例bean 对象。
        Object bean = getSingleton(name);
        // 如果从 单例对象的map容器中 已找到 该beanName对应的 业务对象的实例bean，则直接返回。
        if (bean != null) {
            return bean;
        }
        // 否则，若单例bean对象还未生成，则 调用实现类（DefaultListableBeanFactory）的 方法尝试从beanDefinitinoMap中通过beanName取得 BeanDefinition
        BeanDefinition beanDefinition = getBeanDefinition(name);
        // 次处createBean的方法，其实就是 将 <beanName,beanDefinition的实例> 这条entry ，放到了DefaultSingletonBeanRegistry类中 singletonObjects的map容器中
        return createBean(name, beanDefinition);
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException;

}
