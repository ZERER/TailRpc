package com.tail.rpc.client.async;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 自定义同步组件,用于future的等待
 * @author weidong
 * @date create in 13:42 2018/10/20
 **/
public class Sync extends AbstractQueuedSynchronizer {


    public Sync(){
        this(true);
    }

    public Sync(boolean reverse){
        if (!reverse){
            //反转  设置同步状态
            compareAndSetState(0,1);
        }
    }

    @Override
    protected boolean tryAcquire(int state) {
        //说明还没有获取结果
        if (getState() == 0){
            return false;
        }
        compareAndSetState(1,0);
        return true;
    }

    @Override
    protected boolean tryRelease(int state) {
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
