package cn.bugstack.springframework.test;

import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.support.DefaultListableBeanFactory;
import cn.bugstack.springframework.beans.factory.support.SimpleInstantiationStrategy;
import cn.bugstack.springframework.test.bean.UserService;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class ApiTest {

    @Test
    public void test_BeanFactory() {
        // 1.初始化 BeanFactory ，该beanFactory 缺省的类实例化策略是 cglib代理
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 此处可以手动指定 类的实例化策略，选用 标准JDK代理的方式来实例化对象。
        beanFactory.setInstantiationStrategy(new SimpleInstantiationStrategy());
        // 3. 注入bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);
        // 通过 显示的指定构造参数，来通过cglib代理动态的调用 对应的构造器来创建代理对象
        // 4.获取bean ，此处通过beanFactory.getBean()方法 获得的bean对象已经是 通过cglib 创建的业务bean的代理对象了，
        UserService userService = (UserService) beanFactory.getBean("userService", "小傅哥");
        // 此处的userService 还是作为 cglib的业务bean代理对象
        userService.queryUserInfo();
    }

    @Test
    public void test_cglib() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });
        // 通过cglib的 Enhancer 来创建 业务bean的代理对象
        Object obj = enhancer.create(new Class[]{String.class}, new Object[]{"小傅哥"});
        // 此处的obj 返回的是业务bean的cglib代理对象
        System.out.println(obj);
    }

    @Test
    public void test_newInstance() throws IllegalAccessException, InstantiationException {
        // 正常通过反射 创建业务bean的方式
        UserService userService = UserService.class.newInstance();

        System.out.println(userService);
    }

    @Test
    public void test_constructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<UserService> userServiceClass = UserService.class;
        // 正常通过反射 指定构造参数，来通过反射调用对应 有参数构造的方式来创建业务bean对象
        Constructor<UserService> declaredConstructor = userServiceClass.getDeclaredConstructor(String.class);
        UserService userService = declaredConstructor.newInstance("小傅哥");
        System.out.println(userService);
    }

    @Test
    public void test_parameterTypes() throws Exception {
        Class<UserService> beanClass = UserService.class;
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        Constructor<?> constructor = declaredConstructors[0];
        // 若反射时指定了选用无参构造器，但实际调用反射创建对象时却指定了构造参数，则穿个件对象会报错 非法参数个数异常
        Constructor<UserService> declaredConstructor = beanClass.getDeclaredConstructor(constructor.getParameterTypes());
        UserService userService = declaredConstructor.newInstance("小傅哥");
        System.out.println(userService);
    }

}
