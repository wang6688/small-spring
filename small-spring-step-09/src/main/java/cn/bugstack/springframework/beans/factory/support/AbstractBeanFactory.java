package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.BeanFactory;
import cn.bugstack.springframework.beans.factory.FactoryBean;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.config.BeanPostProcessor;
import cn.bugstack.springframework.beans.factory.config.ConfigurableBeanFactory;
import cn.bugstack.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 * <p>
 * BeanDefinition注册表接口
 */
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    /**
     * ClassLoader to resolve bean class names with, if necessary
     */
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /**
     * BeanPostProcessors to apply in createBean
     */
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return doGetBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return (T) getBean(name);
    }
    /*** 若 该beanName 在 singletonObjects 容器中不存在的话，则在判断该bean是否是一个 FactoryBean，
     * 若是则通过反射代理Proxy 实例化出 该factoryBean的对象，若该FactoryBean被定义为单例的则将其放入到factoryBeanObjectCache 的map容器中，然后返回该对象
     *  若不是 普通的业务对象则直接返回*/
    protected <T> T doGetBean(final String name, final Object[] args) {
        Object sharedInstance = getSingleton(name);
        if (sharedInstance != null) {
            // 如果是 FactoryBean，则需要调用 FactoryBean#getObject
            return (T) getObjectForBeanInstance(sharedInstance, name);
        }

        BeanDefinition beanDefinition = getBeanDefinition(name);
        Object bean = createBean(name, beanDefinition, args);
        // 若 该bean 为 BeanFactory类且 该类实现了FactoryBean接口 ，则(使用反射代理Proxy类)创建 / 获得 该Bean 的 实例化对象，进行返回
        // 否则 意味着该bean 是一个普通的业务bean对象，直接返回该业务bean对象
        return (T) getObjectForBeanInstance(bean, name);
    }

    private Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        // 若 实例化好的且填充完属性且完成初始化的bean的类 没有实现FactoryBean 接口，则证明是一个普通的业务bean，直接返回该实例bean即可。
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }
        // 代码执行到此处，说明 该bean 是一个 BeanFactory的类，且该类实现了FactoryBean 接口，作为一个 factoryBean对象 存在
        // 该实例化好的填充完属性且完成初始化的bean的类 实现了 FactoryBean 接口，则从factoryBeanObjectCache的map容器中查找该bean
        Object object = getCachedObjectForFactoryBean(beanName);
        // 若factoryBeanObjectCache的map容器中 没有找到该bean
        if (object == null) {
            FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
            // (使用反射代理Proxy类)创建 / 获得 该factoryBean 的 实例化对象
            object = getObjectFromFactoryBean(factoryBean, beanName);
        }

        return object;
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * Return the list of BeanPostProcessors that will get applied
     * to beans created with this factory.
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

}
