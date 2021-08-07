package cn.bugstack.springframework.test.bean;

public interface IUserDao {
    /***  此方法其实是执行的 {@link ProxyBeanFactory#getObject}*/
    String queryUserName(String uId);

}
