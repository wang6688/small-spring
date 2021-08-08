package cn.bugstack.springframework.test;

import cn.bugstack.springframework.aop.AdvisedSupport;
import cn.bugstack.springframework.aop.MethodMatcher;
import cn.bugstack.springframework.aop.TargetSource;
import cn.bugstack.springframework.aop.aspectj.AspectJExpressionPointcut;
import cn.bugstack.springframework.aop.framework.Cglib2AopProxy;
import cn.bugstack.springframework.aop.framework.JdkDynamicAopProxy;
import cn.bugstack.springframework.aop.framework.ReflectiveMethodInvocation;
import cn.bugstack.springframework.test.bean.IUserService;
import cn.bugstack.springframework.test.bean.UserService;
import cn.bugstack.springframework.test.bean.UserServiceInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class ApiTest {

    @Test
    public void test_aop() throws NoSuchMethodException {
        // 获得切面 ：解析 切入点表达式
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* cn.bugstack.springframework.test.bean.UserService.*(..))");
        // 通过反射的方式 获得 UserService 实现类的 queryUserInfo 方法
        Class<UserService> clazz = UserService.class;
        Method method = clazz.getDeclaredMethod("queryUserInfo");
        // 切面是否 和 UserService类 匹配
        System.out.println(pointcut.matches(clazz));
        // 切面是否和 UserService类的 queryUserInfo 方法匹配
        System.out.println(pointcut.matches(method, clazz));
    }

    @Test
    public void test_dynamic() {
        // 目标对象： 多态
        IUserService userService = new UserService();
        // 组装代理信息
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 将 IUserService接口的多态实现 设为 被代理的目标对象
        advisedSupport.setTargetSource(new TargetSource(userService));
        // 设置方法的拦截器
        advisedSupport.setMethodInterceptor(new UserServiceInterceptor());
        // 设置切入点表达式的 方法匹配规则
        advisedSupport.setMethodMatcher(new AspectJExpressionPointcut("execution(* cn.bugstack.springframework.test.bean.IUserService.*(..))"));

        // 代理对象(JdkDynamicAopProxy) ：将被代理的目标信息 封装成 JDK动态代理，获得其代理对象，其内部是使用 Proxy.newProxyInstance 获得代理实例对象

        IUserService proxy_jdk = (IUserService) new JdkDynamicAopProxy(advisedSupport).getProxy();
        // 测试调用： 因为 上一行 proxy_jdk 接收的返回结果是一个jdk动态代理对象，所以在执行之前会先执行其代理类中的（JdkDynamicAopProxy::invoke）方法拦截器，之后才会执行真正的业务方法
        System.out.println("测试结果：" + proxy_jdk.queryUserInfo());

        // 代理对象(Cglib2AopProxy)
        IUserService proxy_cglib = (IUserService) new Cglib2AopProxy(advisedSupport).getProxy();
        // 测试调用： 由于上一行proxy_cglib 接收的返回结果是一个cglib动态代理对象，所以执行之前会先执行其代理类中的（Cglib2AopProxy的私有静态内部类DynamicAdvisedInterceptor中的intercept）方法拦截器，之后才会执行真正的业务方法
        System.out.println("测试结果：" + proxy_cglib.register("花花"));
    }

    @Test
    /** 普通的jdk动态代理的逻辑实现，与切面无关 */
    public void test_proxy_class() {
        // jdk 动态代理Proxy.newProxyInstance 的核心逻辑也是通过注解的方式 寻找其接口类的实现，此处获得的userService 的代理对象其实就是 指定的字符串"你被代理了！"
        IUserService userService = (IUserService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{IUserService.class}, (proxy, method, args) -> "你被代理了！");
        /*** 所以此处虽然调用了 userService.queryUserInfo(); 但实际却与{@link UserService#queryUserInfo()}  方法没有任何关系*/
        String result = userService.queryUserInfo();
        // 此处返回的result 只是 手动为其指定的 代理结果"你被代理了！"
        System.out.println("测试结果：" + result);

    }

    @Test
    public void test_proxy_method() {
        // 目标对象(可以替换成任何的目标对象)
        Object targetObj = new UserService();

        // AOP 代理，为 Proxy.newProxyInstance 传入 InvocationHandler的匿名内部类的形式
        IUserService proxy = (IUserService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), targetObj.getClass().getInterfaces(), new InvocationHandler() {
            // 方法匹配器 ： 切面表达式
            MethodMatcher methodMatcher = new AspectJExpressionPointcut("execution(* cn.bugstack.springframework.test.bean.IUserService.*(..))");

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 注意： 此处若切面表达式配置的是接口类 其也能与接口的实现类匹配成功
                // 若切面表达式 与用户要执行的方法 匹配成功， 则先执行 切面拦截器，再由拦截器执行完毕后 调用下一个拦截器/用户要执行的 目标方法
                if (methodMatcher.matches(method, targetObj.getClass())) {
                    // 方法拦截器
                    MethodInterceptor methodInterceptor = invocation -> {
                        long start = System.currentTimeMillis();
                        try {
                            //由拦截器执行完毕后 回调 用户传如的（queryUserInfo）目标方法
                            return invocation.proceed();
                        } finally {
                            System.out.println("监控 - Begin By AOP");
                            System.out.println("方法名称：" + invocation.getMethod().getName());
                            System.out.println("方法耗时：" + (System.currentTimeMillis() - start) + "ms");
                            System.out.println("监控 - End\r\n");
                        }
                    };
                    // 反射调用 ： 先执行拦截器，再由拦截器调用 用户要执行的方法:new ReflectiveMethodInvocation 将queryUserInfo()调用传入，届时在97行代码return invocation.proceed()进行回调
                    return methodInterceptor.invoke(new ReflectiveMethodInvocation(targetObj, method, args));
                }
                // 若用户想要执行的方法与 切面表达式不匹配 ，则直接使用反射调用 用户要执行的目标方法
                return method.invoke(targetObj, args);
            }
        });
        // proxy.queryUserInfo() 会先执行 匿名代理类88行的invoke 方法，由88行的 拦截器调用后回调真正的queryUserInfo() 方法。
        String result = proxy.queryUserInfo();
        System.out.println("测试结果：" + result);

    }

}
