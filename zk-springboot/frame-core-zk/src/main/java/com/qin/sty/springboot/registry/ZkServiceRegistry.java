package com.qin.sty.springboot.registry;

public interface ZkServiceRegistry {

    /**
     * 注册服务信息
     * @param serviceName 服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName, String serviceAddress);

    String getData(String serviceName);
}