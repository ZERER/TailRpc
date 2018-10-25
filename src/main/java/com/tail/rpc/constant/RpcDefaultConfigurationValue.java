package com.tail.rpc.constant;

import java.net.InetSocketAddress;

/**
 * @author weidong
 * @date Create in 17:08 2018/10/12
 **/
public class RpcDefaultConfigurationValue {

    public static final String NAME_SPACE = "tail";

    //public static final String DEFAULT_RPC_NODE="/default";

    public static final String DEFAULT_ZK_ADDR="118.25.45.237:2181";

    public static final int ZK_CONNECT_TIME_OUT = 15 * 1000;

    public static final String ZK_SPILT = "/";

    public static final String STR_SPILT = ":";

    public static final String DEFAULT_SERVER_NAME = "default";

    public static final Integer DEFAULT_WEIGHT = 0;

    public static final InetSocketAddress DEFAULT_INET_ADDR = new InetSocketAddress("127.0.0.1",8080);

    public static final int ZERO = 0;

    public static final long TIME_OUT = 5000;

}
