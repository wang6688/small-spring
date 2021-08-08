package cn.bugstack.springframework.aop.framework;

import cn.bugstack.springframework.aop.AdvisedSupport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB2-based {@link AopProxy} implementation for the Spring AOP framework.
 *
 * <p><i>Requires CGLIB 2.1+ on the classpath.</i>.
 * As of Spring 2.0, earlier CGLIB versions are not supported anymore.
 *
 * <p>Objects of this type should be obtained through proxy factories,
 * configured by an AdvisedSupport object. This class is internal
 * to Spring's AOP framework and need not be used directly by client code.
 */
public class Cglib2AopProxy implements AopProxy {

    private final AdvisedSupport advised;

    public Cglib2AopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    /** 使用这种 cglib 代理（Enhancer）的方式 创建出的代理对象的beanName中会 携带'$$'的字样 */
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        // 设置enhancer代理的 目标实现类
        enhancer.setSuperclass(advised.getTargetSource().getTarget().getClass());
        // 设置 enhancer 要被代理的目标接口类
        enhancer.setInterfaces(advised.getTargetSource().getTargetClass());
        // 当代理对象 执行目标方法之前，会先回调 静态内部类DynamicAdvisedInterceptor动态拦截器中的intercept的方法
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
        return enhancer.create();
    }

    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        // 获得cglib代理对象要执行的目标方法，若配置的切面表达式与代理类中的目标实现类 匹配上，则调用拦截器的方法
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            CglibMethodInvocation methodInvocation = new CglibMethodInvocation(advised.getTargetSource().getTarget(), method, objects, methodProxy);
            // 注意： 此处若切面表达式配置的是接口类 其也能与接口的实现类匹配成功
            if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
                return advised.getMethodInterceptor().invoke(methodInvocation);
            }
            return methodInvocation.proceed();
        }
    }

    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
            super(target, method, arguments);
            this.methodProxy = methodProxy;
        }

        @Override  // 用户拦截器执行此方法 放行到 用户要执行的目标方法中
        public Object proceed() throws Throwable {
            return this.methodProxy.invoke(this.target, this.arguments);
        }

    }

}
