package com.qin.sty.springboot;

import com.qin.sty.springboot.registry.ZkServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ServletComponentScan
@SpringBootApplication
public class Application {

    @Autowired
    private ZkServiceRegistry serviceRegistry;

    @RequestMapping(name="go",path="/go")
    public String go(String serviceName){
        String url=serviceRegistry.getData(serviceName);
        return "url is "+url;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
