package com.tail.rpc.model;

import com.tail.rpc.util.GsonUtils;
import com.tail.rpc.util.ProtostuffUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author weidong
 * @date create in 19:43 2018/10/24
 **/
@Data
@Slf4j
public class Information {

    private String id;

    private String serverName;

    private String address;

    private Integer port;

    private Integer weight;

    private String remark;

    public byte[] toDate() {
        check();
        log.info("发布服务信息:{}",GsonUtils.to(this));
        return ProtostuffUtils.serializer(this);


    }

    private void check() {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(serverName) || address == null || port == null || weight == null){
            throw  new RuntimeException("service 信息不完整");
        }
    }

    public static void main(String[] args) {
        Information information = new Information();
        information.setServerName("1");
        information.setWeight(0);
        information.setRemark("re");
        information.setAddress("123");
        information.setPort(100);
        information.setId("123");
        String data = GsonUtils.to(information);
        System.out.println(data);

        byte[] datas = ProtostuffUtils.serializer(information);
        System.out.println(new String(datas));
        Information in  = ProtostuffUtils.deserializer(datas, Information.class);
        System.out.println(in);


    }
}
