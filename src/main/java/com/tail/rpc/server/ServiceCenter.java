//package com.tail.server;
//
//import RpcException;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
///**
// * @author weidong
// * @date Create in 15:32 2018/10/11
// **/
//@Slf4j
//public class ServiceCenter {
//
//    private final Map<String,Object> serverMap = new HashMap<>();
//
//    public int size(){
//        return serverMap.size();
//    }
//
//    public Set<String> getServiceName(){
//        return serverMap.keySet();
//    }
//
//    public Object getService(String key){
//        return serverMap.get(key);
//    }
//
//
//    public void addService(RpcConfiguration configuration) {
//       if (configuration.getServiceSize() <=0 ){
//           throw new NullPointerException("service");
//       }
//        configuration.getServiceMap().forEach((serviceName,serviceBean)->{
//            if (containsServer(serviceName)) {
//                throw new RpcException("服务已存在");
//            }
//            serverMap.put(serviceName, serviceBean);
//        });
//    }
//
//
//    public boolean containsServer(String interfaceClass){
//        return serverMap.containsKey(interfaceClass);
//    }
//
//    /**
//     * 单例模式
//     */
//    public static ServiceCenter instance(){
//        return ServiceCenterClass.serviceCenter;
//    }
//    private ServiceCenter(){}
//
//    static class ServiceCenterClass{
//        static ServiceCenter serviceCenter = new ServiceCenter();
//    }
//}
