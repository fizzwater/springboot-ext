package com.qin.sty.springboot.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class ZkServiceRegistryImpl implements ZkServiceRegistry {

    private final String REGISTRY_PATH = "/registry";
    private static CountDownLatch latch = new CountDownLatch(1);

    private CuratorFramework curatorFramework;

    public ZkServiceRegistryImpl() {
        log.debug("初始化类 ZkServiceRegistryImpl ");
    }

    public ZkServiceRegistryImpl(String zkServers) {
        try {
            RetryPolicy policy = new RetryNTimes(3,  1000);
            curatorFramework= CuratorFrameworkFactory.newClient(zkServers, 60 * 1000, 30 * 1000, policy);
            try {
                curatorFramework.start();
                latch.countDown();
            } catch (Exception e) {
                log.error("ZkOnlineClientUtil Failed to connect with {} , error {}", zkServers, e);
                throw e;
            }
            latch.await();
            log.debug("connected to zookeeper");

        } catch (Exception e) {
            log.error("create zookeeper client failuer", e);
        }
    }

    @Override
    public String getData(String serviceName){
        try {
            String servicePath=REGISTRY_PATH+"/"+serviceName;
            log.debug("service_path is :"+servicePath);
            Stat stat = curatorFramework.checkExists().forPath(servicePath);
            if(stat==null){
                log.error("not exist service_path  :"+servicePath);
                return "";
            }
            List<String> childPath = curatorFramework.getChildren().forPath(servicePath);

            if(childPath==null||childPath.size()==0){
                log.error("%s address node is not exsited",serviceName);
                return "";
            }
            String addressPath=servicePath+"/";
            if(childPath.size()==1){
                addressPath+=childPath.get(0);
            }
            if(childPath.size()>1){
                addressPath+=childPath.get((int)(Math.random()*childPath.size()));
            }
            log.debug("address node is "+addressPath);
            byte[] data=curatorFramework.getData().forPath(addressPath);
            return new String(data,"UTF-8");
        } catch (Exception e) {
            log.error("get data failure",e);
        }
        return "";
    }


    @Override
    public void register(String serviceName, String serviceAddressContent) {
        String registryPath = REGISTRY_PATH;
        try {
            //创建根节点：持久节点
            Stat stat = curatorFramework.checkExists().forPath(registryPath);
            if (stat == null) {
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(registryPath);
                log.debug("create registry node:{}",registryPath);
            }
            //创建服务节点：持久节点
            String servicePath=registryPath+"/"+serviceName;
            stat = curatorFramework.checkExists().forPath(servicePath);
            if(stat==null){
                curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(servicePath);
                log.debug("create service node :{}"+servicePath);
            }
            //创建地址节点：临时顺序节点
            String addressPath=servicePath+"/address-";
            curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(addressPath,serviceAddressContent.getBytes());
            log.debug("create node address:{}=>{}",addressPath,serviceAddressContent);
        } catch (Exception e) {
            log.error("create node failure",e);
        }

    }


}
