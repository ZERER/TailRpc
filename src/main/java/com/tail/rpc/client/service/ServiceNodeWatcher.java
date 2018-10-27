package com.tail.rpc.client.service;

import com.tail.rpc.exception.RpcException;
import com.tail.rpc.model.Information;
import com.tail.rpc.thread.RpcThreadPool;
import com.tail.rpc.util.GsonUtils;
import com.tail.rpc.util.ProtostuffUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import static com.tail.rpc.constant.RpcDefaultConfigurationValue.ZK_SPILT;

/**
 * @author weidong
 * @date create in 21:26 2018/10/25
 **/
@Slf4j
public class ServiceNodeWatcher {
    /**
     * 已经注册事件的节点
     */
    private final List<String> alreadyWatchNodes = new LinkedList<>();
    /**
     * zookeeper客户端
     */
    private final CuratorFramework zkClient;
    /**
     * 本地服务中心
     */
    private LocalServer localServer = LocalServer.instance();
    /**
     * 注册事件的线程池
     */
    private final static ThreadPoolExecutor EXECUTOR = RpcThreadPool.getWatchDefaultExecutor();
    /**
     * 已经注册事件的节点管理
     */
    private final Map<String, PathChildrenCache> watcher = new ConcurrentHashMap<>();

    public ServiceNodeWatcher(CuratorFramework zkClient) {
        this.zkClient = zkClient;
        try {
            watchNode(zkClient.getChildren().forPath(ZK_SPILT));
        } catch (Exception e) {
            log.error("warning... zk 监听节点出现问题");
            throw new RpcException("watch");
        }
    }

    /**
     * 监听命名空间指定名称的节点
     *
     * @param watchNode 指定名称的节点
     */
    public void watchNode(String watchNode) {
        if (!alreadyWatchNodes.contains(watchNode)) {
            List<String> watchNodes = new ArrayList<>(1);
            watchNodes.add(watchNode);
            watchNode(watchNodes);
        }
    }

    /**
     * 监听命名空间下所有的节点
     *
     * @param watchNodes 所有的节点信息
     */
    private void watchNode(List<String> watchNodes) {
        watchNodes.removeAll(alreadyWatchNodes);
        watchNodes.forEach(this::doWatchNode);
        alreadyWatchNodes.addAll(watchNodes);
    }

    private void doWatchNode(String watchNode) {
        final PathChildrenCache childrenCache = new PathChildrenCache(zkClient, ZK_SPILT + watchNode, true);
        watcher.put(watchNode, childrenCache);
        try {
            childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
            childrenCache.getListenable().addListener((client, event) -> {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        addedEventOperation(event);
                        break;
                    case CHILD_UPDATED:
                        updatedEventOperation(event);
                        break;
                    case CHILD_REMOVED:
                        removedEventOperation(event);
                        break;
                    default:
                        break;
                }
            }, EXECUTOR);
        } catch (Exception e) {
            log.error("warning... 监听节点:{}失败", watchNode);
        }
    }

    private void updatedEventOperation(PathChildrenCacheEvent event) {
        //todo 更新节点 暂无操作
    }

    private void removedEventOperation(PathChildrenCacheEvent event) {
        final Information information = ProtostuffUtils.deserializer(event.getData().getData(), Information.class);
        final String service = event.getData().getPath().split(ZK_SPILT)[1];
        ServiceBean delService = new ServiceBean(information);
        localServer.delService(service, delService);
        log.info("节点:{}删除节点信息:{}", service, GsonUtils.to(information));
        if (localServer.serviceIsEmpty(service)) {
            cancalWatch(service);
        }
    }

    private void addedEventOperation(PathChildrenCacheEvent event) {
        final Information information = ProtostuffUtils.deserializer(event.getData().getData(), Information.class);
        final String service = event.getData().getPath().split(ZK_SPILT)[1];
        ServiceBean addService = new ServiceBean(information);
        localServer.addService(service, addService);
        log.info("节点:{}新增子节点信息:{}", service, GsonUtils.to(information));
    }

    private void cancalWatch(String service) {
        try {
            log.info("取消对节点:{}的监听",service);
            watcher.get(service).close();
        } catch (IOException e) {
            log.error("warning... 取消监听节点:{}失败",service);
        }
    }


}
