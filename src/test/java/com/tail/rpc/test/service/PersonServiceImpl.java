package com.tail.rpc.test.service;

import com.tail.rpc.annotation.RpcService;

/**
 * @author weidong
 * @date create in 15:54 2018/10/27
 **/
@RpcService(Value = PersonService.class)
public class PersonServiceImpl implements PersonService {
    @Override
    public Integer add(int a, int b) {
        return a+b;
    }
}
