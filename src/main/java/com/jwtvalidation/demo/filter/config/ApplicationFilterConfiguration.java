package com.jwtvalidation.demo.filter.config;

import com.jwtvalidation.demo.filter.JwtAuthenticationFilter;
import com.jwtvalidation.demo.security.service.JwksService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationFilterConfiguration {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setUrlPatterns(
                List.of(
                        "/demo/data"
                )
        );
        return registrationBean;
    }

    @Bean
    public JwtAuthenticationFilter jwtFilter(JwksService jwksService) {
        return new JwtAuthenticationFilter(jwksService);
    }
}
