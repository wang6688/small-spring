package cn.bugstack.springframework.beans;

import java.util.ArrayList;
import java.util.List;

/**
 该类，封装了 PropertyValue 为一个 list，用来存储 业务bean的 多个 属性信息
 */
public class PropertyValues {
    //
    private final List<PropertyValue> propertyValueList = new ArrayList<>();
    // 手动添加 单个 属性信息
    public void addPropertyValue(PropertyValue pv) {
        this.propertyValueList.add(pv);
    }

    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }

    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

}
