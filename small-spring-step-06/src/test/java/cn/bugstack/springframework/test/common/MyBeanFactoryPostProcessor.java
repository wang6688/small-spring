package cn.bugstack.springframework.test.common;

import cn.bugstack.springframework.beans.BeansException;
import cn.bugstack.springframework.beans.PropertyValue;
import cn.bugstack.springframework.beans.PropertyValues;
import cn.bugstack.springframework.beans.factory.ConfigurableListableBeanFactory;
import cn.bugstack.springframework.beans.factory.config.BeanDefinition;
import cn.bugstack.springframework.beans.factory.config.BeanFactoryPostProcessor;
/**该BeanFactoryPostProcess，用于在beanDefinition对象加载完后 修改  其中的属性 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 自定义的beanDefinition处理器，从 beanDefinitionMap中通过beanName 获取指定的 beanDefinition对象
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("userService");
        // 获取beanName对应 beanDefinition对象的所有属性信息
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        // 为 beanDefinition对象 添加属性信息
        propertyValues.addPropertyValue(new PropertyValue("company", "改为：字节跳动"));
    }

}
