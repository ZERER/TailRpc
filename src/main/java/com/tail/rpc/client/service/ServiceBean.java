package com.tail.rpc.client.service;

import com.tail.rpc.model.Information;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weidong
 * @date create in 20:42 2018/10/15
 **/
@Getter
public class ServiceBean {

    private final InetSocketAddress inetAddress;
    private final Integer weight;
    private final String serverName;
    private final String remark;
    private AtomicInteger num = new AtomicInteger(0);

    public ServiceBean(Information information){
        this.inetAddress = new InetSocketAddress(information.getAddress(),information.getPort());
        this.remark = information.getRemark();
        this.serverName = information.getServerName();
        this.weight = information.getWeight();
        num.getAndIncrement();
    }

    public Integer getAndIncrement(){
        return num.getAndIncrement();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ServiceBean that = (ServiceBean) o;
        return Objects.equals(inetAddress, that.inetAddress) && Objects.equals(weight, that.weight) && Objects.equals(serverName, that.serverName) && Objects.equals(remark, that.remark) && Objects.equals(num, that.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inetAddress, weight, serverName, remark, num);
    }
}
