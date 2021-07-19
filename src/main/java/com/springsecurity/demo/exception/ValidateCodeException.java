package com.springsecurity.demo.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author xiawen yang
 * @date 2021/7/19 下午4:06
 */
public class ValidateCodeException extends AuthenticationException {

    private static final long serialVersionUID = 5022575393500654458L;

    public ValidateCodeException(String message) {
        super(message);
    }
}
