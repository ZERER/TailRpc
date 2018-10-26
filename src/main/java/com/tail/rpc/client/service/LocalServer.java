package com.tail.rpc.client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 本地服务管理中心
 *
 * @author weidong
 * @date create in 14:02 2018/10/13
 **/
public class LocalServer {

    private final Map<String, List<ServiceBean>> serverMap = new ConcurrentHashMap<>();


    /**
     * 获取服务列表
     *
     * @param service service
     * @return 服务列表
     */
    public List<ServiceBean> getService(String service) {
        List<ServiceBean> beans = serverMap.get(service);
        return beans != null ? beans : new ArrayList<>();
    }

    /**
     * 获取服务列表
     *
     * @param service    service
     * @param serverName serverName
     * @return 服务列表
     */
    public List<ServiceBean> getServiceByServerName(String service, String serverName) {
        List<ServiceBean> serviceList = getService(service);
        return serviceList.stream().filter(s -> s.getServerName().equals(serverName)).collect(Collectors.toList());
    }

    /**
     * 判断本地服务是否为空
     *
     * @return true if serverMap is null
     */
    public boolean isEmpty() {
        return serverMap.isEmpty();
    }


    /**
     * 单例模式
     *
     * @return LocalServer实例
     */
    public static LocalServer instance() {
        return RegisterDiscoveryClass.localServer;
    }

    private LocalServer() {
    }

    public void addService(String service, ServiceBean serviceBean) {
        List<ServiceBean> serviceBeanList = getService(service);
        serviceBeanList.add(serviceBean);
        updateService(service, serviceBeanList);
    }

    public void updateService(String service, List<ServiceBean> serviceBeanList) {
        serverMap.put(service, serviceBeanList);
    }

    public void delService(String service, ServiceBean serviceBean) {
        List<ServiceBean> serviceBeanList = getService(service);
        serviceBeanList.remove(serviceBean);
        updateService(service, serviceBeanList);
    }

    public boolean serviceIsEmpty(String service) {
        return getService(service).isEmpty();
    }

    static class RegisterDiscoveryClass {
        static LocalServer localServer = new LocalServer();
    }

}
