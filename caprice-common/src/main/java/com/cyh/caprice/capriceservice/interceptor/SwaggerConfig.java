package com.cyh.caprice.capriceservice.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 崔宇浩
 * @date
 * @time 14:51
 * @desc Swagger配置类，该类里面的应该是固定的，主要用来设置文档的主题信息，比如文档的大标题，副标题，公司名
 * 等
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /*帮助我们生成接口文档
     * 1.配置文档信息
     * 2.配置生成规则
     * 3.
     * */
    /*封装接口文档信息*/
    @Bean
    public Docket getDocket(){
        //创建封面信息对象
        ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
        apiInfoBuilder.title("微信公共服务类")
                .description("!!!")
                .version("v 2. 9.1");

        ApiInfo apiInfo = apiInfoBuilder.build();

        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)//指定生成的文档中的封面信息
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wechat.wechatservice.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
}
