package com.li.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.li.common.annotation.AutoFill;
import com.li.common.constant.JwtClaimsConstant;
import com.li.common.constant.MessageConstant;
import com.li.common.properties.JwtProperties;
import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.common.utils.JwtUtil;
import com.li.common.utils.PasswordUtil;
import com.li.pojo.dto.AdminRegisterDto;
import com.li.pojo.dto.LoginDto;
import com.li.pojo.entity.Admin;
import com.li.pojo.entity.User;
import com.li.pojo.vo.LoginVo;
import com.li.server.mapper.AdminMapper;
import com.li.server.mapper.UserMapper;
import com.li.server.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired private AdminMapper adminMapper;
    @Autowired private JwtProperties jwtProperties;
    @Autowired private UserMapper userMapper;
    @Override
    public Result<LoginVo> login(LoginDto loginDto) {
        log.info("name: {}, password: {}", loginDto.getName(), loginDto.getPassword());
        // 根据用户名查询用户
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getAdminName, loginDto.getName());
        Admin admin = adminMapper.selectOne(lambdaQueryWrapper);
        if (admin == null) {
            return Result.fail(MessageConstant.ADMIN_NOT_EXIST); // 管理员不存在
        }
        if (!PasswordUtil.checkPassword(loginDto.getPassword(), admin.getPassword())) {
            return Result.fail(MessageConstant.ADMIN_PASSWORD_ERROR); // 密码错误
        }
        // 登入成功 返回jwt令牌
        Map<String, Object> claims = new TreeMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, admin.getId());
        claims.put(JwtClaimsConstant.ADMIN_NAME, admin.getAdminName());
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);
        return Result.success(new LoginVo(token));
    }
    @AutoFill
    @Override
    public Result<LoginVo> register(AdminRegisterDto registerDto) {
        if (registerDto == null) {
            return Result.fail(MessageConstant.REGISTER_FAIL); // 注册失败
        }
        if (registerDto.getName() == null || registerDto.getName().length() > 18) {
            return Result.fail(MessageConstant.NAME_ILLEGAL); // 用户名非法
        }
        if (registerDto.getPassword() == null || registerDto.getPassword().length() < 4 || registerDto.getPassword().length() > 16) {
            return Result.fail(MessageConstant.PASSWORD_ILLEGAL); // 密码非法
        }
        if (registerDto.getPhone() == null || registerDto.getPhone().length() != 11) {
            return Result.fail(MessageConstant.PHONE_ILLEGAL); // 手机号非法
        }
        // 根据用户名查询用户
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getAdminName, registerDto.getName());
        Admin admin = adminMapper.selectOne(lambdaQueryWrapper);
        if (admin != null) {
            return Result.fail(MessageConstant.ADMIN_EXIST); // 管理员已存在
        }
        admin = Admin.builder()
                .adminName(registerDto.getName())
                .password(PasswordUtil.encryptPassword(registerDto.getPassword()))
                .phone(registerDto.getPhone())
                .createdTime(registerDto.getCreatedTime())
                .build();
        adminMapper.insert(admin);
        Map<String, Object> claims = new TreeMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, admin.getId());
        claims.put(JwtClaimsConstant.ADMIN_NAME, admin.getAdminName());
        LoginVo loginVo = new LoginVo(JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims));
        return Result.success(loginVo);
    }

    @Override
    public Result<String> updateStatus(Long id, Integer status) {
        log.info("id: {}, status: {}", id, status);
        if (id == null || status == null) {
            return Result.fail(MessageConstant.UNKNOWN_ERROR); // 未知错误
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.fail(MessageConstant.USER_NOT_EXIST); // 用户不存在
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success();
    }

    @Override
    public Result<PageResult<User>> page(Integer page, Integer pageSize, String name) {
        log.info("page: {}, pageSize: {}, name: {}", page, pageSize, name);

        Page<User> pageInfo = new Page<>(page, pageSize);


        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(User::getUsername, name);
        Page<User> users = userMapper.selectPage(pageInfo, lambdaQueryWrapper);

        log.info("total: {}", users.getTotal());
        PageResult<User> pageResult = new PageResult<>(users.getTotal(), users.getRecords());
        return Result.success(pageResult);
    }

    @Override
    public Result<String> delete(Long id) {
        if (id == null) {
            return Result.fail(MessageConstant.UNKNOWN_ERROR); // 未知错误
        }
        userMapper.deleteById(id);
        return Result.success();
    }
}
