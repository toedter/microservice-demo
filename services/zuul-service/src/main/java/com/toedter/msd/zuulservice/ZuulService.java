package com.toedter.msd.zuulservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ZuulService {

    public static void main(String[] args) {
        SpringApplication.run(ZuulService.class, args);
    }
}