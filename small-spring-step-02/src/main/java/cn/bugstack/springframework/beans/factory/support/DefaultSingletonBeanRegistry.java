package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;

/**
  负责 单例 bean的 获取和注册 --  缺省的实现类
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private Map<String, Object> singletonObjects = new HashMap<>();

    @Override
    /**
     * 单例 bean的 获取
     * 该方法实现自 SingletonBeanRegistry 接口
     * */
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }
    /***
     * 单例bean的注册（将<beanName,singletonObject业务对象实例> 放到单例bean对象的map容器中）
     * */
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

}
