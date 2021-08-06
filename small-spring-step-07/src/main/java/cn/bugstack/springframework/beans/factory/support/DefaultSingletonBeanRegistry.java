package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.DisposableBean;
import cn.bugstack.springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    // 该对象 存放 用cglib或jdk动态代理反射实例化好并填充好属性的beanName->bean对象
    private Map<String, Object> singletonObjects = new HashMap<>();
    // 该对象存放用于
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }
    /**
     * 该方法中添加的 与registerShutdownHook*/
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }
    /** 将disposableBeans 中的 业务bean 逐个取出调用销毁方法  **/
    public void destroySingletons() {
        Set<String> keySet = this.disposableBeans.keySet();
        Object[] disposableBeanNames = keySet.toArray();
        
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            // 后放入的 到 disposableBeans 容器种的 bean 先行销毁
            Object beanName = disposableBeanNames[i];
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                // 此处 由于disposableBean 是一个接口，所以会先调用其实现类DisposableBeanAdapter 的destroy方法，
                // 然后再由其DisposableBeanAdapter 实现类调用具体业务bean的 destory()方法，执行真正的销毁动作。
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '" + beanName + "' threw an exception", e);
            }
        }
    }

}
