package com.springsecurity.demo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xiawen yang
 * @date 2021/7/19 下午2:44
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper mapper;

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //自定义登录成功逻辑，这里设定返回json串，串中包含权限信息
        //httpServletResponse.setContentType("application/json;charset=utf-8");
        //httpServletResponse.getWriter().write(mapper.writeValueAsString(authentication));

        //登录成功后，页面将重定向回引发跳转的页面（重定向后，url发生变化）
        SavedRequest savedRequest = requestCache.getRequest(httpServletRequest, httpServletResponse);
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/success.html");
    }
}
