package cn.bugstack.springframework.beans.factory.config;

/**
  可以简单的理解为  业务类的包装类
 */
public class BeanDefinition {

    private Class beanClass;

    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
    }
    /**  获得真正业务对象的 类*/
    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
}
