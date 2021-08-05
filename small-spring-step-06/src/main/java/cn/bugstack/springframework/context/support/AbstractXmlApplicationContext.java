package cn.bugstack.springframework.context.support;

import cn.bugstack.springframework.beans.factory.support.DefaultListableBeanFactory;
import cn.bugstack.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * Convenient base class for {@link cn.bugstack.springframework.context.ApplicationContext}
 * implementations, drawing configuration from XML documents containing bean definitions
 * understood by an {@link cn.bugstack.springframework.beans.factory.xml.XmlBeanDefinitionReader}.
 *
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {

    @Override
    // xmlBean的读取解析类 读取 xml资源的配置文件
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
        // 该 getConfigLocations() 方法实际是调用的 其实现子类ClassPathXmlApplicationContext中由构造器传入的xml资源配置文件路径
        String[] configLocations = getConfigLocations();
        if (null != configLocations){
            // 用 xmlBeanDefinition的读取解析类去 读取并解析 xml资源配置文件中的bean节点等信息，将其封装到 beanDefinition对象中，并放入到beanDefinitionMap容器中
            beanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    protected abstract String[] getConfigLocations();

}
