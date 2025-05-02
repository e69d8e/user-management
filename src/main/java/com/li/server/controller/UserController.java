package com.li.server.controller;

import com.li.common.result.Result;
import com.li.pojo.dto.LoginDto;
import com.li.pojo.dto.UserRegisterDto;
import com.li.pojo.dto.UserUpdateDto;
import com.li.pojo.vo.LoginVo;
import com.li.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
//@CrossOrigin(origins = "*", allowCredentials = "true")
public class UserController {
    @Autowired private UserService userService;

    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/register")
    public Result<LoginVo> register(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.register(userRegisterDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody UserUpdateDto userUpdateDto) {
        return userService.update(userUpdateDto);
    }

    // 注销
    @DeleteMapping("/logout")
    public Result<String> logout() {
        return userService.delete();
    }
}
