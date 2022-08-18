package com.cyh.caprice.capriceservice.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: cuiyuhao
 * @Description:
 * @Data: Created in 15:39 2021/10/12
 **/
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**").excludePathPatterns(
                "/SendWechat/getAutograph",
                "/swagger-ui.html/**",
                "/doc.html/**",
                "/swagger-resources/**",
                "/v2/**",
                "/webjars/**"
        );
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}
