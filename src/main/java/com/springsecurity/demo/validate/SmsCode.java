package com.springsecurity.demo.validate;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xiawen yang
 * @date 2021/7/19 下午5:01
 * 短信验证
 */
@Data
public class SmsCode {
    private String code;
    private LocalDateTime expireTime;

    public SmsCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public SmsCode(String code, LocalDateTime expireTime) {
        this.code = code;
        this.expireTime = expireTime;
    }

    boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
