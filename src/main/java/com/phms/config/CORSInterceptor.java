package com.phms.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author LinChaoFan
 * @Date 2022/4/22 10:17
 * @Version 1.0
 */
@Component

public class CORSInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //添加跨域CORS
//        服务器不在一起，下面这个跨域是马川 给我的，好用
        String originHeader = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", originHeader);
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,accessToken");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");



        return true;
    }
}

