package cn.bugstack.springframework.test;

import cn.bugstack.springframework.BeanDefinition;
import cn.bugstack.springframework.BeanFactory;
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
        // 1.初始化 BeanFactory
        BeanFactory beanFactory = new BeanFactory();

        // 2.注入业务bean 到BeanDefinition中
        BeanDefinition beanDefinition = new BeanDefinition(new UserService());
        // beanFactory中有一个 Map<业务bean的Name,beanDefinition>
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 3.获取bean, 通过业务bean的Name可以从beanFactory中获得 业务的bean对象
        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.queryUserInfo();
    }

}
