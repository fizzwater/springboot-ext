package com.qin.sty.springboot.framework;

import com.qin.sty.springboot.registry.ZkServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;
import java.util.Set;


@Slf4j
@Component
//@WebListener
public class RegistServiceWebListener implements ServletContextListener{
    @Value("${server.address}")
    private String serverAddress;
    @Value("${server.port}")
    private int serverPort;
    @Value("${server.name}")
    private String serverName;

    @Autowired
    private ZkServiceRegistry serviceRegistry;

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        //获取请求映射
        ServletContext servletContext=event.getServletContext();
        ApplicationContext applicationContext=WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        RequestMappingHandlerMapping mapping=applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo,HandlerMethod> infoMap=mapping.getHandlerMethods();
        for(RequestMappingInfo info:infoMap.keySet()){
            String serviceName=info.getName();
            log.debug("-----------"+serviceName);
            if(null!=serviceName){
                PatternsRequestCondition prc = info.getPatternsCondition();
                Set<String> patterns = prc.getPatterns();
                for (String uStr : patterns) {
                    if(uStr.startsWith("/")){
                        uStr = uStr.substring(1);
                    }
                    serviceRegistry.register(serviceName,String.format("%s:%d/%s/%s", serverAddress,serverPort,serverName,uStr) );
                }


            }
        }
    }

}