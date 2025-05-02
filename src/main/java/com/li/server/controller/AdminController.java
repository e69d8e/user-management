package com.li.server.controller;

import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.pojo.dto.AdminRegisterDto;
import com.li.pojo.dto.LoginDto;
import com.li.pojo.entity.User;
import com.li.pojo.vo.LoginVo;
import com.li.server.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
//@CrossOrigin(origins = "*", allowCredentials = "true")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginDto loginDto) {
        return adminService.login(loginDto);
    }

    @PostMapping("/register")
    public Result<LoginVo> register(@RequestBody AdminRegisterDto registerDto) {
        return adminService.register(registerDto);
    }

    // 启用和禁用用户账号
    @PutMapping
    public Result<String> updateStatus(Long id, Integer status) {
        return adminService.updateStatus(id, status);
    }

    // 分页查询用户账号
    @GetMapping("/page")
    public Result<PageResult<User>> page(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                         @RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize,
                                         @RequestParam(name = "name", defaultValue = "") String name) {
        return adminService.page(page, pageSize, name);
    }

    // 删除用户账号
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return adminService.delete(id);
    }
}
