package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.PropertyValue;
import cn.bugstack.springframework.beans.PropertyValues;
import cn.bugstack.springframework.beans.factory.DisposableBean;
import cn.bugstack.springframework.beans.factory.InitializingBean;
import cn.bugstack.springframework.beans.factory.config.AutowireCapableBeanFactory;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.config.BeanPostProcessor;
import cn.bugstack.springframework.beans.factory.config.BeanReference;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Abstract bean factory superclass that implements default bean creation,
 * with the full capabilities specified by the class.
 * Implements the {@link cn.bugstack.springframework.beans.factory.config.AutowireCapableBeanFactory}
 * interface in addition to AbstractBeanFactory's {@link #createBean} method.
 *
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
            // 给 Bean 填充属性： 对实例化出的业务bean的代理对象 进行属性值（取自beanDefinition）填充
            applyPropertyValues(beanName, bean, beanDefinition);
            // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        // 注册实现了 DisposableBean 接口的 Bean 对象 ， 将bean放入到 disposableBeans 的map容器中
        // 前提条件： 1. 业务bean必须实现了DisposableBean接口，
        // 或者 2. xml资源配置文件中指定了destroy-method 属性，且destroy-method指向的方法在业务bean中必须存在
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);
        //  当 业务bean 调用代理类 1.实例化好了--> 2.属性值填充好了--> 3.在初始化前调用后置处理器执行一些操作（选填）-->
        //  4.执行初始化操作【必要操作】（1.若业务bean的类实现了InitializingBean 接口，则可以调用业务bean的afterPropertiesSet方法{实现自InitializingBean接口方法}执行一些必要的操作
        //                              2.若xml的配置文件中 手工指定了初始化的方法名，则也会调用业务bean中对应的 初始化方法 进行初始化）
        // 5. 将经过这一系列实例化初始化处理后的 业务bean 对象放入到 singletonObjects的map容器中。
        addSingleton(beanName, bean);
        return bean;
    }
    /**  若 业务bean的类实现了 DisposableBean 接口，
     *  或者 业务bean中有销毁bean的处理方法，且在xml配置文件中指定了销毁bean的方法名,
     *  则将 该业务bean 注册到 （当前类其父类AbstractBeanFactory的父类DefaultSingletonBeanRegistry中的）disposableBeans 的map容器中，便于在类销毁时，回调该销毁方法。
     *  该方法执行后 会与 (applicationContext::registerShutdownHook)DefaultSingletonBeanRegistry::destroySingletons 方法前后呼应
     * */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
            // registerDisposableBean 调用的其实是  其爷爷类的方法，该方法会将 当前业务bean 放入到 其爷爷类的disposableBeans 的map容器中
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructorToUse = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 寻找beanDefinition 对象中 指定的构造器 来调用cglib代理/jdk代理反射 出 业务bean的实例化代理对象
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        for (Constructor ctor : declaredConstructors) {
            if (null != args && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    /**
     * Bean 属性填充
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {

                String name = propertyValue.getName();
                Object value = propertyValue.getValue();

                if (value instanceof BeanReference) {
                    // A 依赖 B，获取 B 的实例化
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }
                // 属性填充
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception e) {
            throw new BeansException("Error setting property values：" + beanName);
        }
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 1. 执行 BeanPostProcessor Before 处理 ：若业务bean的后置处理器存在，则调用后置处理器进行 业务bean的加工操作
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 执行 Bean 对象的初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Invocation of init method of bean[" + beanName + "] failed", e);
        }

        // 2. 执行 BeanPostProcessor After 处理 : 在此案例7中 由于并没有为业务bean 指定后置处理器，所以不会调用后置处理器执行操作
        wrappedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        return wrappedBean;
    }
    /** 该方法的逻辑中 提供了2 种 不同的初始化bean的 方法：
     *     1.  业务bean的类实现了InitializingBean 接口，则调用其 afterPropertiesSet 方法进行初始化。
     *     2.   用户在xml配置文件中 手动指定了 初始化的方法名，所以也会再调用 业务bean中的对应的初始化方法 进行初始化。
     *
     *     这2种方式，可以同时存在。**/
    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        // 1. 实现接口 InitializingBean ： 若业务bean 的类实现了InitializingBean接口，
        //则可以在 上述业务bean实例化填充好属性值后，再进行 一些用户自定义行为的其他操作。
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }

        // 2. 注解配置 init-method {判断是为了避免二次执行销毁}
        // 若业务bean 配置了初始化方法，且 xml配置文件中 指定了 其初始化的方法名，则spring会再 调用 业务bean指定的初始化方法
        String initMethodName = beanDefinition.getInitMethodName();
        if (StrUtil.isNotEmpty(initMethodName)) {
            Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            if (null == initMethod) {
                throw new BeansException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
            }
            //spring会再 调用 业务bean指定的初始化方法
            initMethod.invoke(bean);
        }
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        //  若 用户指定 了业务bean的后置处理器，则依次调用后置处理器对 用代理类 实例化好并初始化好的业务对象， 调用后置处理器执行一些 后置处理的操作
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }

}
