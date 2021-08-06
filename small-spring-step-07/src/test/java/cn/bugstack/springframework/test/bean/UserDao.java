package cn.bugstack.springframework.test.bean;

import cn.bugstack.springframework.beans.factory.DisposableBean;
import cn.bugstack.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

public class UserDao  {

    private static Map<String, String> hashMap = new HashMap<>();
    // 该方法 由于在 xml的资源配置文件中被 指定为 初始化的方法，所以在该bean被初始化时，会调用该方法
    public void initDataMethod(){
        System.out.println(this.getClass().getSimpleName()+"执行：init-method");
        hashMap.put("10001", "小傅哥");
        hashMap.put("10002", "八杯水");
        hashMap.put("10003", "阿毛");
    }
    // 该方法 由于在 xml的资源配置文件中被 指定为 销毁的方法，所以在该bean被销毁时，会调用该方法
    public void destroyDataMethod(){
        System.out.println(this.getClass().getSimpleName()+"执行：destroy-method");
        hashMap.clear();
    }

    public String queryUserName(String uId) {
        return hashMap.get(uId);
    }

//    @Override
//    public void destroy() throws Exception {
//        System.out.println(this.getClass().getSimpleName()+"执行：DisposableBean::destroy");
//    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println(this.getClass().getSimpleName()+"执行：InitializingBean::afterPropertiesSet");
//    }
}
