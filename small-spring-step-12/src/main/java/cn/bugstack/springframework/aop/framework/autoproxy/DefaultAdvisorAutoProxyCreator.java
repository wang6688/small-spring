package cn.bugstack.springframework.aop.framework.autoproxy;

import cn.bugstack.springframework.aop.*;
import cn.bugstack.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import cn.bugstack.springframework.aop.framework.ProxyFactory;
import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.BeanFactory;
import cn.bugstack.springframework.beans.factory.BeanFactoryAware;
import cn.bugstack.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import cn.bugstack.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collection;

/**
 * BeanPostProcessor implementation that creates AOP proxies based on all candidate
 * Advisors in the current BeanFactory. This class is completely generic; it contains
 * no special code to handle any particular aspects, such as pooling aspects.
 * <p>
  由于该类 实现了 InstantiationAwareBeanPostProcessor 接口的父接口BeanPostProcessor ，所以spring 可以在 AbstractApplicationContext类中的refresh()方法的
 第5步中将. BeanPostProcessor的实现类  注册到 beanPostProcessors的 list容器中。 这样 在
 由于该类实现了 InstantiationAwareBeanPostProcessor 接口，所以在 spring执行 createBean的方法是会将 实现了 InstantiationAwareBeanPostProcessor接口的后置处理器
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override // 该方法 才是为 业务bean 织入 AOP代理拦截器的 关键方法！！！！
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        // 若beanClass 是AOP代理、切入点、通知等 相关接口的实现类，则什么都不做
        if (isInfrastructureClass(beanClass)) return null;
        // 若beanClass 时业务bean 对象 （非AOP代理、切入点、通知等 相关接口的实现类），则获得 AOP的切面对象实例
        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();  // 获得切入点对象
            if (!classFilter.matches(beanClass)) continue;

            AdvisedSupport advisedSupport = new AdvisedSupport();

            TargetSource targetSource = null;
            try {
                targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance()); // 为bean 通过反射构造器的方式实例化出一个对象作为拦截的目标对象
            } catch (Exception e) {
                e.printStackTrace();
            }
            advisedSupport.setTargetSource(targetSource);
            // 设置通知类型为 方法拦截器
            advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
            advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
            // 设置使用 JDK动态代理的方式 来 实例化业务bean对象
            advisedSupport.setProxyTargetClass(false);

            return new ProxyFactory(advisedSupport).getProxy();

        }

        return null;
    }
    // 判断beanClass 是不是 AOP代理、切入点、通知等相关接口的实现类
    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass);
    }

    @Override
    // 在bean初始化之前 织入 aop代理的后置处理器之前 作一些事情
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    // 在bean初始化之后的织入 aop代理后置处理器 时作一些事情
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    
}
