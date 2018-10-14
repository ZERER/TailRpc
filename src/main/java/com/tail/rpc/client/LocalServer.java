package com.tail.rpc.client;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weidong
 * @date create in 14:02 2018/10/13
 **/
public class LocalServer {

    private final Map<String, List<InetSocketAddress>> serverMap = new ConcurrentHashMap<>();


    public void putServer(String serverNode,List<InetSocketAddress> serverAddr){
        serverMap.put(serverNode,serverAddr);
    }

    public List<InetSocketAddress> getService(String serverNode){
        return serverMap.get(serverNode);
    }

    public int size(){
        return serverMap.size();
    }

    public void removeService(String service,List<InetSocketAddress> serverNode){
        List<InetSocketAddress> serverNodes = serverMap.get(service);
        if (!serverNode.isEmpty()){
            serverNodes.removeAll(serverNode);
        }
        serverMap.put(service,serverNodes);

    }

    public static LocalServer instance(){
        return RegisterDiscoveryClass.localServer;
    }
    private LocalServer(){}
    static class RegisterDiscoveryClass{
        static LocalServer localServer = new LocalServer();
    }

}
