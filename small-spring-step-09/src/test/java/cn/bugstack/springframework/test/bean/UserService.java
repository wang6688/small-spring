package cn.bugstack.springframework.test.bean;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
public class UserService {

    private String uId;
    private String company;
    private String location;
    // userDao 对象的 是采用ProxyBeanFactory  类 匿名的方式 进行实例化的，
    // 该属性的赋值时机是： 当ProxyBeanFactory的 bean对象被实例化好后，再调用 FactoryBeanRegistrySupport::getObjectFromFactoryBean 方法，进而才得以执行它的ProxyBeanFactory的getObject()方法。
    private IUserDao userDao;

    public String queryUserInfo() {
        // 此处 的userDao 由于是使用的 ProxyBeanFactory 匿名 实例化出的对象，所以queryUserName 最终执行的是ProxyBeanFactory 类中的getObject 方法。
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

    public IUserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }
}
