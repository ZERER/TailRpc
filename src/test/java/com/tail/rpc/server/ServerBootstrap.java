package com.tail.rpc.server;

import com.tail.rpc.service.HelloServiceImpl;

import java.lang.reflect.InvocationTargetException;

/**
 * @desc:
 * @author: weidong
 * @date: create in 10:08 2018/10/13
 **/
public class ServerBootstrap {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {


        RpcServerBootstrap bootstrap = new RpcServerBootstrap();
        bootstrap.register("118.25.45.237:2181")
                .addService(new HelloServiceImpl())
                .serverAddr("127.0.0.1:8080")
                .start();



//        Class<?> clazz = Class.forName("com.tail.rpc.service.HelloService");
//        Method method = clazz.getMethod("hello", Class.forName("java.lang.Integer"));
//        String result = (String) method.invoke(new HelloServiceImpl(),new Object[]{10});
//        System.out.println(result);


    }
}
