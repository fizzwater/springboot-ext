package com.qin.sty.springboot;

import com.qin.sty.springboot.framework.RegistServiceWebListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContextListener;

@RestController
@ServletComponentScan
@SpringBootApplication
public class Application {
    @RequestMapping(name="HelloService",path="/hello")
    public String hello(){
        return "hello craig";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
	public ServletListenerRegistrationBean<ServletContextListener> servletContextListenerBean(RegistServiceWebListener registServiceWebListener){
		ServletListenerRegistrationBean<ServletContextListener>
		sessionListener = new ServletListenerRegistrationBean<ServletContextListener>(registServiceWebListener);
		return sessionListener;
	}

}

