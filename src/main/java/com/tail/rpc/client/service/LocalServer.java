package com.tail.rpc.client.service;

import org.apache.zookeeper.server.quorum.ServerBean;

import java.net.InetSocketAddress;
import java.sql.Wrapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weidong
 * @date create in 14:02 2018/10/13
 **/
public class LocalServer {

    private final Map<String, List<ServiceBean>> serverMap = new ConcurrentHashMap<>();


    public List<ServiceBean> putServer(String serverNode,List<InetSocketAddress> serverAddr){
        List<ServiceBean> serviceBeans = wrapToServiceBean(serverAddr);
        serverMap.put(serverNode,serviceBeans);
        return serviceBeans;
    }

    public List<ServiceBean> getService(String serverNode){
        return serverMap.get(serverNode);
    }

    public int size(){
        return serverMap.size();
    }

    public void removeService(String service,List<InetSocketAddress> serverNode){
        List<ServiceBean> serviceBeans = wrapToServiceBean(serverNode);
        List<ServiceBean> localServers = serverMap.get(service);

        if (!localServers.isEmpty()){
            localServers.removeAll(serviceBeans);
        }
        serverMap.put(service,localServers);

    }

    public static List<InetSocketAddress> wrapToInetSockertAddress(List<ServiceBean> serviceBeans){
        List<InetSocketAddress> addresses = new LinkedList<>();
        serviceBeans.forEach(address->{
            addresses.add(address.getInetAddress());
        });
        return addresses;
    }

    public static List<ServiceBean> wrapToServiceBean(List<InetSocketAddress> serverAddr){
        List<ServiceBean> serviceBeans = new LinkedList<>();
        serverAddr.forEach((server)->{
            serviceBeans.add(new ServiceBean(server));
        });
        return serviceBeans;
    }



    public static LocalServer instance(){
        return RegisterDiscoveryClass.localServer;
    }
    private LocalServer(){}
    static class RegisterDiscoveryClass{
        static LocalServer localServer = new LocalServer();
    }

}
