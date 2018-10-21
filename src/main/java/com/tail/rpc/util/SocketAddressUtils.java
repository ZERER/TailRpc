package com.tail.rpc.util;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * @author weidong
 * @date create in 20:01 2018/10/14
 **/
public class SocketAddressUtils {

    public static InetSocketAddress warp(String internetAddress){
        String[] socketAddress = internetAddress.split(":");
        InetSocketAddress address = new InetSocketAddress(socketAddress[0],Integer.valueOf(socketAddress[1]));
        return address;
    }

    public static List<InetSocketAddress> warp(List<String> serverNodes) {
        List<InetSocketAddress> socketAddresses = new LinkedList<>();

        serverNodes.forEach(serverNode->{
            socketAddresses.add(warp(serverNode));
        });
        return socketAddresses;
    }

}
