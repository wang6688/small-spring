package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.FactoryBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Support base class for singleton registries which need to handle
 * {@link cn.bugstack.springframework.beans.factory.FactoryBean} instances,
 * integrated with {@link DefaultSingletonBeanRegistry}'s singleton management.
 * <p>
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

    /**
     * Cache of singleton objects created by FactoryBeans: FactoryBean name --> object
     * 该map容器 专门用于 缓存 BeanFactory （一般是通过 反射代理Proxy类 生成的）单例对象的实例
     */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    /***则从factoryBeanObjectCache的map容器中查找该bean */
    protected Object getCachedObjectForFactoryBean(String beanName) {
        Object object = this.factoryBeanObjectCache.get(beanName);
        return (object != NULL_OBJECT ? object : null);
    }
    /**  (使用反射代理Proxy类)创建/获得  该 BeanFactory 的实例对象 */
    protected Object getObjectFromFactoryBean(FactoryBean factory, String beanName) {
        if (factory.isSingleton()) {
            //若 beanFactory 是 单例的 ，且factoryBeanObjectCache 的map容器中没有缓存该bean对应的 实例对象，
            //     * 则调用 反射包中的（Proxy） 代理类，新建一个出 beanFactory类的实例对象 ,然后将其 放入到factoryBeanObjectCache 的map容器中 缓存起来
            //     * 最后 返回 该 bean实例对象
            Object object = this.factoryBeanObjectCache.get(beanName);
            if (object == null) {
                object = doGetObjectFromFactoryBean(factory, beanName);
                this.factoryBeanObjectCache.put(beanName, (object != null ? object : NULL_OBJECT));
            }
            return (object != NULL_OBJECT ? object : null);
        } else {
            // 若 beanFactory 不是单例的，则直接调用 反射包中的（Proxy） 代理类，新建一个出 beanFactory类的实例对象 ,最后 返回 该对象
            return doGetObjectFromFactoryBean(factory, beanName);
        }
    }
    /***  使用反射包中的Proxy 代理类，实例化出一个 FactoryBean 对象，该对象的内容 是 InvocationHandler 的实现逻辑 */
    private Object doGetObjectFromFactoryBean(final FactoryBean factory, final String beanName){
        try {
            return factory.getObject();
        } catch (Exception e) {
            throw new BeansException("FactoryBean threw exception on object[" + beanName + "] creation", e);
        }
    }

}
