package cn.bugstack.springframework.test;

import cn.bugstack.springframework.beans.PropertyValue;
import cn.bugstack.springframework.beans.PropertyValues;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.config.BeanReference;
import cn.bugstack.springframework.beans.factory.support.DefaultListableBeanFactory;
import cn.bugstack.springframework.test.bean.UserDao;
import cn.bugstack.springframework.test.bean.UserService;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**

 */
public class ApiTest {

    @Test
    public void test_BeanFactory() {
        // 1.初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. UserDao 注册
        beanFactory.registerBeanDefinition("userDao", new BeanDefinition(UserDao.class));

        // 3. UserService 设置属性[uId、userDao]
        PropertyValues propertyValues = new PropertyValues();  // 创建一个属性组对象
        propertyValues.addPropertyValue(new PropertyValue("uId", "10001")); // 将对象的uId属性信息放到属性组对象中
        // 将对象的 引用userDao属性对象信息放到 属性组信息中
        propertyValues.addPropertyValue(new PropertyValue("userDao",new BeanReference("userDao")));

        // 4. UserService 注入bean ，将上面创建的 属性组信息 注入到 beanDefinition中 用于为 业务bean创建好对象后填充属性值
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class, propertyValues);
        // 通过 beanFactory 将 userService的业务bean 放入到 beanDefinitionMap的 容器对象中。
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 5. UserService 获取bean ， 在初始获得bean对象时会 采用cglib代理或jdk动态代理反射的方式创建实例化对象，
        // 第二次获取bean的话则是从 singleObjects的map容器中获取
        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.queryUserInfo();
    }

}
