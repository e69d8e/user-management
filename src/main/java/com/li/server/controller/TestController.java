package com.li.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {
    @GetMapping("/test")
    public String testMethod() {
//        int a = 1 / 0; // 测试
        log.info("testMethod");
        return "hello";
    }
}