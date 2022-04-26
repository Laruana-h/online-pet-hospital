package com.phms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author LinChaoFan
 * @Date 2022/4/22 10:18
 * @Version 1.0
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired //解决跨域
    private CORSInterceptor corsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(corsInterceptor);

        super.addInterceptors(registry);

    }
}

