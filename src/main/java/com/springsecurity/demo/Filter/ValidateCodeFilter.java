package com.springsecurity.demo.Filter;

import com.springsecurity.demo.controller.ValidateController;
import com.springsecurity.demo.exception.ValidateCodeException;
import com.springsecurity.demo.validate.ImageCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.springsecurity.demo.exception.ValidateCodeException;

/**
 * @author xiawen yang
 * @date 2021/7/19 下午4:09
 * 我们都知道，Spring Security实际上是由许多过滤器组成的过滤器链，处理用户登录逻辑的过滤器为UsernamePasswordAuthenticationFilter，
 * 而验证码校验过程应该是在这个过滤器之前的，即只有验证码校验通过后采去校验用户名和密码。由于Spring Security并没有直接提供验证码校验相关的
 * 过滤器接口，所以我们需要自己定义一个验证码校验的过滤器ValidateCodeFilter
 * ValidateCodeFilter继承了org.springframework.web.filter.OncePerRequestFilter，该过滤器只会执行一次。
 */
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //拦截url为login的post请求，该路径对应登录form表单的登录接口，执行验证码的校验逻辑，其他请求直接放行
        if(StringUtils.equalsIgnoreCase("/login", request.getRequestURI()) &&
            StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            try {
                validateCode(new ServletWebRequest(request));
            } catch (ValidateCodeException e) {
                failureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        //放行
        filterChain.doFilter(request, response);
    }

    private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(servletWebRequest, ValidateController.SESSION_KEY_IMAGE_CODE);
        String codeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "imageCode");

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码不能为空");
        }
        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在！");
        }
        if (codeInSession.isExpire()) {
            sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY_IMAGE_CODE);
            throw new ValidateCodeException("验证码已过期！");
        }
        if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不正确！");
        }
        sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY_IMAGE_CODE);
    }

}
