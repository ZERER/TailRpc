package com.tail.rpc.model;

import com.tail.rpc.util.GsonUtils;
import com.tail.rpc.util.ProtostuffUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

/**
 * @author weidong
 * @date create in 19:43 2018/10/24
 **/
@Data
public class Information {

    private String id;

    private String serverName;

    private InetSocketAddress address;

    private Integer weight;

    private String remark;

    public byte[] toDate() {
        check();
        return GsonUtils.to(this).getBytes();


    }

    private void check() {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(serverName) || address == null || weight == null){
            throw  new RuntimeException("service 信息不完整");
        }
    }

    public static void main(String[] args) {
        Information information = new Information();
        information.setServerName("1");
        information.setWeight(0);
        information.setRemark("re");
        information.setAddress(new InetSocketAddress("127.0.0.1",8080));
        information.setId("123");
        String data = GsonUtils.to(information);
        System.out.println(data);
        System.out.println(information.getAddress());

        byte[] datas = ProtostuffUtils.serializer(information);

        Information in  = ProtostuffUtils.deserializer(datas, Information.class);
        System.out.println(in);


    }
}
