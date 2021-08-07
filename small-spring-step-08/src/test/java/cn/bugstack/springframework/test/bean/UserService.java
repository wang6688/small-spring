package cn.bugstack.springframework.test.bean;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.factory.*;
import cn.bugstack.springframework.context.ApplicationContext;
import cn.bugstack.springframework.context.ApplicationContextAware;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class UserService implements BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, BeanFactoryAware {
    //  用于保存spring运行时的 aoolicationContext 对象
    private ApplicationContext applicationContext;
    // 用于保存spring运行时的 beanFacotry 对象
    private BeanFactory beanFactory;

    private String uId;
    private String company;
    private String location;
    private UserDao userDao;

    @Override
    /**  用于在spring容器的运行时 可以获得其beanFactory 到 该业务bean的对象上 ，该方法实现自  BeanFactoryAware 接口
     * 该方法会在 实例化好bean 并且填充好属性后，在 初始化阶段的第一步 回调该方法 ，之后是调用 业务bean的后置处理器（初始化方法前） ，执行bean的初始化方法(回调InitializingBean接口的方法)，调用 业务bean的后置处理器（初始化方法后）*/
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    /**用于在 spring容器的运行时 可以获得其 applicationContext 到该业务 bean的对象 上 ，该方法实现自 ApplicationContextAware 接口
     *  该方法会之所以能够得到执行，是因为在AbstractApplicationContext::refresh()方法中的第3步将 ApplicationContextAwareProcessor添加为bean的后置处理器了。
     *  该方法会在实例化好bean 并且填充好属性后，在 bean调用  初始化方法之前 调用 bean的后置处理器 来回调此方法*/
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    /** 用于在 spring容器的运行时，可以获得该业务bean 的beanName ，该方法实现自BeanNameAware 接口
     *      * 该方法会在 实例化好bean 并且填充好属性后，在 初始化阶段的第一步 回调该方法 ，之后是调用 业务bean的后置处理器（初始化方法前） ，执行bean的初始化方法(回调InitializingBean接口的方法)，调用 业务bean的后置处理器（初始化方法后）*/
    public void setBeanName(String name) {
        System.out.println("Bean Name is：" + name);
    }

    @Override
    /** 用于在 spring容器的运行时，可以获得 其业务bean的 类加载器 ，该方法实现自 BeanClassLoaderAware 接口
     *      * 该方法会在 实例化好bean 并且填充好属性后，在 初始化阶段的第一步 回调该方法 ，之后是调用 业务bean的后置处理器（初始化方法前） ，执行bean的初始化方法(回调InitializingBean接口的方法)，调用 业务bean的后置处理器（初始化方法后） */
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println("ClassLoader：" + classLoader);
    }

    public String queryUserInfo() {
        return userDao.queryUserName(uId) + "," + company + "," + location;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

}
