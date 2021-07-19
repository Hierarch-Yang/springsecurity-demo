package com.springsecurity.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiawen yang
 * @date 2021/7/17 下午3:51
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/hello")
    public String hello() {
        return "hello admin";
    }
}
