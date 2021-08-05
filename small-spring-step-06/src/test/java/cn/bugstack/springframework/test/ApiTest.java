package cn.bugstack.springframework.test;

import cn.bugstack.springframework.beans.factory.support.DefaultListableBeanFactory;
import cn.bugstack.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import cn.bugstack.springframework.context.support.ClassPathXmlApplicationContext;
import cn.bugstack.springframework.test.bean.UserService;
import cn.bugstack.springframework.test.common.MyBeanFactoryPostProcessor;
import cn.bugstack.springframework.test.common.MyBeanPostProcessor;
import org.junit.Test;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class ApiTest {

    @Test
    public void test_BeanFactoryPostProcessorAndBeanPostProcessor(){
        // 1.初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 将 beanFactory 注入到xmlBean的解析读取器中，方便将xml解析后的bean数据 放入到 beanFactory中的 beanDefinitionMap容器中
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
//        读取解析xml配置文件&注册BeanDefinition到 beanDefinitionMap 容器中
        reader.loadBeanDefinitions("classpath:spring.xml");

        // 3. BeanDefinition 加载完成 & Bean实例化之前，修改 BeanDefinition 的属性值
        MyBeanFactoryPostProcessor beanFactoryPostProcessor = new MyBeanFactoryPostProcessor();
        // 用自定义的 beanFactory后置处理器，修改 beanDefinitionMap中的beanDefinition对象的属性
        beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

        // 4. Bean实例化之后，修改 Bean 属性信息
        MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();
        // 注入自定义的bean后置处理器 到 beanFactory对象中，
        // 便于后面执行 doGetBean方法动作时，实例化出业务bean对象并填充好属性后，让其再自动调用该后置处理器
        beanFactory.addBeanPostProcessor(beanPostProcessor);

        // 5. 获取Bean对象调用方法
        // 该getBean的动作，第一次需要从beanDefinitionMap中 取出beanDefinition对象并调用其业务bean的构造器实例化出对象填充属性，
        // 经过后置处理器的加工操作后，会放入到singletonObjects的map容器中，当下次或以后再调用getBean时，直接从 singletonObjects的map容器中获取即可。 所以是单例的
        UserService userService = beanFactory.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
    }

    @Test
    public void test_xml() {
        // 1.初始化 BeanFactory，读取并解析xml配置文件 ，将bean节点信息解析成 beanDefinition对象，并放入到beanDefinitionMap的容器中
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:springPostProcessor.xml");

        // 2. 获取Bean对象调用方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
    }

}
