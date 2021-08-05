package cn.bugstack.springframework.context.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.ConfigurableListableBeanFactory;
import cn.bugstack.springframework.beans.factory.config.BeanFactoryPostProcessor;
import cn.bugstack.springframework.beans.factory.config.BeanPostProcessor;
import cn.bugstack.springframework.context.ConfigurableApplicationContext;
import cn.bugstack.springframework.core.io.DefaultResourceLoader;

import java.util.Map;

/**
 * Abstract implementation of the {@link cn.bugstack.springframework.context.ApplicationContext}
 * interface. Doesn't mandate the type of storage used for configuration; simply
 * implements common context functionality. Uses the Template Method design pattern,
 * requiring concrete subclasses to implement abstract methods.
 * <p>
 * 抽象应用上下文
 * <p>
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    @Override
    public void refresh() throws BeansException {
        // 1. 创建 BeanFactory，并加载 BeanDefinition：创建beanFactory，并读取解析xml资源配置文件，将bean节点信息封装成beanDefinition对象注入到beanDefinition的Map容器中
        refreshBeanFactory();

        // 2. 获取 BeanFactory，刚方法其实是读取的 步骤1 的refreshBeanFactory() 方法中创建的beanFactory 对象
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        // 3. 在 Bean 实例化之前，执行 BeanFactoryPostProcessor (Invoke factory processors registered as beans in the context.)
        // 对 beanFacotry的后置处理器进行实例化操作，并将其放入到singletonObjects的map中，以使其可以对 beanDefinition中的属性进行修改
        invokeBeanFactoryPostProcessors(beanFactory);

        // 4. BeanPostProcessor 需要提前于其他 Bean 对象实例化之前执行注册操作
        // 对bean的 后置处理器进行实例化操作，并将其放入到singletonObjects的map中，将业务bean的后置处理器对象 添加到bean的后置处理器组中
        registerBeanPostProcessors(beanFactory);

        // 5. 提前实例化业务的单例Bean对象，在此动作中也会对业务bean执行后置处理器操作修改实例化的业务bean中的属性， 经过 该动作后所有的业务bean对象就都已经放入到了singletonObjects的map容器中了，
        // 所以下次调用getBean方法时，就是从 singletonObjects的map容器中直接获取了。
        beanFactory.preInstantiateSingletons();
    }

    protected abstract void refreshBeanFactory() throws BeansException;

    protected abstract ConfigurableListableBeanFactory getBeanFactory();
    // 对 后置处理器 的bean 进行实例化，并放入到singletonObjects 的map中
    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 从 beanDefinitionMap中 读取BeanFactoryPostProcessor 的 实现类（beanFactory的后置处理器，来自于xml的配置文件中），并将其实现类进行实例化并放入到singletonObjects的map容器中
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
//   将beanFactory对象传入beanFactory的后置处理器，使其可以处理beanFactory中的beanDefinitionMap中的beanDefinition对象在加载完后 修改  其中的属性
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 从 beanDefinitionMap中 读取 BeanPostProcessor的 实现类（业务bean的后置处理器，来自于xml的配置文件中），并将其实现类进行实例化后放入singletonObjects的map容器中
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            //   将业务bean的后置处理器对象 添加到bean的后置处理器组中，
            //  --  使其可以在稍后进行处理 一些 经过beanDefinition中的业务bean经过构造器实例化好后的代理类对象，又经过属性填充后的对象， 执行一些必要的后置处理操作
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return getBeanFactory().getBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }

}
