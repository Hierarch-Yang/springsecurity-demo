package com.springsecurity.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiawen yang
 * @date 2021/7/17 下午4:11
 */
@RestController
@RequestMapping("/sale")
public class SaleController {

    @GetMapping("/hello")
    public String hello() {
        return "hello sale";
    }

    @GetMapping("/getIds")
    public String getIds() {
        return "Ids... ...";
    }
}
