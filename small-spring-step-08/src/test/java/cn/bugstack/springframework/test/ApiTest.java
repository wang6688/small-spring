package cn.bugstack.springframework.test;

import cn.bugstack.springframework.context.support.ClassPathXmlApplicationContext;
import cn.bugstack.springframework.test.bean.UserService;
import org.junit.Test;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class ApiTest {

    @Test
    public void test_xml() {
        // 1.初始化 BeanFactory
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        // 为业务bean（userDao注册 销毁时的回调方法）
        applicationContext.registerShutdownHook();

        // 2. 获取Bean对象调用方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
        // 由于 userService 实现了ApplicationContextAware 接口，并且在 刷新上下文的方法中第3步 将ApplicationContextAwareProcessor注册为后置处理器，
        // 所以在初始化bean方法时可以回调后置处理器，从而使的可以在userService 中直接获得applicationContext对象。
        System.out.println("ApplicationContextAware："+userService.getApplicationContext());
        // 由于 userService 实现了 BeanFactoryAware 接口，所以使的 在实例化完对象并且填充好属性后 在初始化方法中 可以先行回调userService的setBeanFactory方法,
        // 所以 才可以在userService 的类中 直接 获得到 beanFactory 对象。
        System.out.println("BeanFactoryAware："+userService.getBeanFactory());

    }

}
