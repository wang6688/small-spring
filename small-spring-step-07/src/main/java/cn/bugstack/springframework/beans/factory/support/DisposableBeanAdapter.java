package cn.bugstack.springframework.beans.factory.support;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.DisposableBean;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Method;

/**
 * Adapter that implements the {@link DisposableBean} and {@link Runnable} interfaces
 * performing various destruction steps on a given bean instance:
 * <p>
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class DisposableBeanAdapter implements DisposableBean {

    private final Object bean;
    private final String beanName;
    private String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    /***在JVM 关闭前 会先执行此方法，因为其被注册为  Runtime.getRuntime().addShutdownHook() 系统函数的回调
     *  该方法支持2种类型的销毁：
     *  1. 实现了 DisposableBean 接口的类
     *  2. 通过xml配置文件手工指定 销毁方法名，并且销毁方法在业务bean中存在。
     *
     * */
    public void destroy() throws Exception {
        // 1. 实现接口 DisposableBean: 若业务bean实现了DisposableBean 接口则调用业务bean中的destroy方法 执行销毁动作。
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
        }

        // 2. 注解配置 destroy-method {判断是为了避免二次执行销毁}：
        // 若业务bean 既实现了DisposableBean，又在xml配置文件中 手工指定了销毁方法名（且方法名不为"destroy"）时，可以调用用户自定义的销毁方法
        if (StrUtil.isNotEmpty(destroyMethodName) && !(bean instanceof DisposableBean && "destroy".equals(this.destroyMethodName))) {
            Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
            if (null == destroyMethod) {
                throw new BeansException("Couldn't find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
            }
            destroyMethod.invoke(bean);
        }
        
    }

}
