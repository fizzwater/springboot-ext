package com.qin.sty.springboot.registry;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConfigurationProperties(prefix="zk")
public class RegistryConfig {

    private String servers;

    @Bean
    public ZkServiceRegistry serviceRegistry(){
        return new ZkServiceRegistryImpl(servers);
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}