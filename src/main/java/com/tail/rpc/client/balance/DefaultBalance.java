package com.tail.rpc.client.balance;

import com.tail.rpc.client.ClientRegister;
import com.tail.rpc.client.LocalServer;
import com.tail.rpc.exception.RpcServiceNotFindException;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weidong
 * @date create in 14:51 2018/10/13
 **/
public class DefaultBalance implements RpcBalance{


    private static AtomicInteger index = new AtomicInteger(1);

    private LocalServer localServer = LocalServer.instance();

    private final ClientRegister zkClient;

    public DefaultBalance(ClientRegister clientRegister) {
        zkClient = clientRegister;
    }


    @Override
    public InetSocketAddress select(String server) {
        List<InetSocketAddress> serverNodes;
        if(localServer.size() > 0){
            //本地查找
            serverNodes = localServer.getService(server);
            if (serverNodes.size() > 0){
                InetSocketAddress serverAddr = select(serverNodes);
                if (serverAddr != null){
                    return serverAddr;
                }
            }
        }
        //去注册中心查找服务
        serverNodes = zkClient.getServer(server);
        if (serverNodes.size() > 0){
            InetSocketAddress serverAddr = select(serverNodes);
            if (serverAddr != null){
                return serverAddr;
            }
        }

        throw new RpcServiceNotFindException(server);

    }


    private InetSocketAddress select(List<InetSocketAddress> serverList){
        if (serverList == null || serverList.size() == 0){
            throw new NullPointerException("server");
        }
        return serverList.get(index.getAndIncrement() % serverList.size());
    }


}
