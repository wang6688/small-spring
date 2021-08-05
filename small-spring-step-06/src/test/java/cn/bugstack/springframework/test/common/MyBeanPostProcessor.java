package cn.bugstack.springframework.test.common;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.config.BeanPostProcessor;
import cn.bugstack.springframework.test.bean.UserService;
/**
 * 该类用于
 * **/
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    /*** 对该bean 在 放入到singleoObjects的map容器中之前， 执行一些预初始化的动作
     * @param bean  该bean目前是 刚调用cglib代理或jdk动态代理实例化好并填充好属性的bean，但此时尚未放入到 singletonObjects中
     * **/
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("userService".equals(beanName)) {
            UserService userService = (UserService) bean;
            userService.setLocation("改为：北京");
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
