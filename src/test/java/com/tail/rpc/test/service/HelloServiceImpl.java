package com.tail.rpc.test.service;

import com.tail.rpc.annotation.RpcService;

/**
 * @desc:
 * @author: weidong
 * @date: create in 10:19 2018/10/13
 **/
@RpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(Integer x) {

        System.out.println("hello " + x);
        return "RPC Server :" + x;
    }
}
