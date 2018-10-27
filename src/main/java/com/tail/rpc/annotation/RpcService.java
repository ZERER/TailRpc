package com.tail.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author weidong
 * @date Create in 10:15 2018/10/11
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
    /**
     * @return 发布的服务接口
     */
    Class<?> Value() default Class.class;
}
