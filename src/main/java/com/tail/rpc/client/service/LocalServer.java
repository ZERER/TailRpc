package com.tail.rpc.client.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author weidong
 * @date create in 14:02 2018/10/13
 **/
public class LocalServer {

    private final Map<String, List<ServiceBean>> serverMap = new ConcurrentHashMap<>();

    public List<ServiceBean> getService(String service){
        return serverMap.get(service);
    }

    public List<ServiceBean> getServiceByServerName(String service,String serverName){
        List<ServiceBean> serviceList = getService(service);
        return serviceList
                .stream()
                .filter(s -> s.getServerName().equals(serverName))
                .collect(Collectors.toList());
    }

    public int size(){
        return serverMap.size();
    }


    public static LocalServer instance(){
        return RegisterDiscoveryClass.localServer;
    }
    private LocalServer(){}
    static class RegisterDiscoveryClass{
        static LocalServer localServer = new LocalServer();
    }

}
