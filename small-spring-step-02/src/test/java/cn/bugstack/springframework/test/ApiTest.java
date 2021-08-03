package cn.bugstack.springframework.test;

import cn.bugstack.springframework.beans.factory.BeanFactory;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.support.DefaultListableBeanFactory;
import cn.bugstack.springframework.test.bean.UserService;
import org.junit.Test;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class ApiTest {

    @Test
    public void test_BeanFactory(){
        // 1.初始化 BeanFactory ，存放着beanName->BeanDefinition的 一个Map
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2.注册 bean ，将业务类包装成 beanDefinition,
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        // 并手动指定业务类的beanName和 beanDefinition 注册到 beanFactory中的beanDefinitionMap容器里
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 3.第一次获取 bean , 其实是调用的beanFactory对象的爷爷类（当前对象类DefaultListableBeanFactory的父类AbstractAutowireCapableBeanFactory的父类）AbstractBeanFactory的getBean方法的实现
        // 第一次获取 其实是通过 beanDefinition中的业务对象的类class 反射创建出来的，创建后将其放到  singletonObjects的map中
        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.queryUserInfo();

        // 4.第二次获取 bean from Singleton， 此处获得的bean 是从 singletonObjects的map中获取的
        UserService userService_singleton = (UserService) beanFactory.getBean("userService");
        userService_singleton.queryUserInfo();
    }

}
