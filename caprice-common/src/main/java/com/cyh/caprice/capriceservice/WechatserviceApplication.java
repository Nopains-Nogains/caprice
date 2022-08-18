package com.cyh.caprice.capriceservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan(basePackages = "com.wechat.*.dao")
public class WechatserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatserviceApplication.class, args);
    }

}
