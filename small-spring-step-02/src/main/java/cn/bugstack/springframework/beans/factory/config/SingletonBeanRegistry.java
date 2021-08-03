package cn.bugstack.springframework.beans.factory.config;

/**

 *
 * 单例注册表 ，只声明 一个通过 beanName 获得单例对象的 抽象方法
 */
public interface SingletonBeanRegistry {

    Object getSingleton(String beanName);

}
