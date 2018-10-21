package com.tail.rpc.client.async;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 自定义同步组件,用于future的等待
 * @author weidong
 * @date create in 13:42 2018/10/20
 **/
public class Sync extends AbstractQueuedSynchronizer {

    @Override
    protected boolean tryAcquire(int arg) {
        //说明还没有获取结果
        if (getState() == 0){
            return false;
        }
        return true;
    }

    @Override
    protected boolean tryRelease(int arg) {
        //已经释放锁了，或者释放锁成功
        if (getState() == 1 || compareAndSetState(0,1)) {
            return true;
        }
        return false;
    }

    public boolean isFinish(){
        return getState() == 1;
    }

}
