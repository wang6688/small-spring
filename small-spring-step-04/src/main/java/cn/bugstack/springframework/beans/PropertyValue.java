package cn.bugstack.springframework.beans;

/**
 * 单纯的一组 bean的属性信息，如 变量名-> 基本类型的数据值  或 变量名 ->引用类型的数据
 *
 * bean 属性信息
 */
public class PropertyValue {

    private final String name;

    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

}
