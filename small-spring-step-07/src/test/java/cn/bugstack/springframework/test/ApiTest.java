package cn.bugstack.springframework.test;

import cn.bugstack.springframework.context.ApplicationContext;
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
        // 注册销毁的回调方法，便于JVM在关闭时 先行将 业务bean 销毁（内存清理、对象销毁）后在 关闭JVM
        applicationContext.registerShutdownHook();

        // 2. 获取Bean对象调用方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
        // 在方法结束前JVM 关闭时，会先调用  DefaultSingletonBeanRegistry::destroySingletons 方法（调用方式为applicationContext.registerShutdownHook()为其注册了回调）
         // 进行 业务bean对象的内存清理、对象销毁等动作 执行完后，在真正的关闭JVM
//        applicationContext.close();  //也可以手动调用该方法关闭 容器
    }

    @Test
    public void test_hook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("close！")));
    }

}
