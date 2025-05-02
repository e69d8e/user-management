package com.li.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.li.common.annotation.AutoFill;
import com.li.common.constant.JwtClaimsConstant;
import com.li.common.constant.MessageConstant;
import com.li.common.properties.JwtProperties;
import com.li.common.result.Result;
import com.li.common.utils.JwtUtil;
import com.li.common.utils.PasswordUtil;
import com.li.common.utils.ThreadLocalUtil;
import com.li.pojo.dto.LoginDto;
import com.li.pojo.dto.UserRegisterDto;
import com.li.pojo.dto.UserUpdateDto;
import com.li.pojo.entity.User;
import com.li.pojo.vo.LoginVo;
import com.li.server.mapper.UserMapper;
import com.li.server.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserMapper userMapper;
    @Autowired private JwtProperties jwtProperties;
    @Autowired private ThreadLocalUtil threadLocal;

    @Override
    public Result<LoginVo> login(LoginDto loginDto) {
        // 根据用户名查询用户
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, loginDto.getName());
        User user = userMapper.selectOne(lambdaQueryWrapper);

        if (user == null) {
            return Result.fail(MessageConstant.USER_NOT_EXIST); // 用户不存在
        }
//        String password = PasswordUtil.encryptPassword(loginDto.getPassword());
        if (!PasswordUtil.checkPassword(loginDto.getPassword(), user.getPassword())) {
            return Result.fail(MessageConstant.USER_PASSWORD_ERROR); // 密码错误
        }
        if (user.getStatus().equals(0)) {
            return Result.fail(MessageConstant.USER_DISABLE); // 用户状态异常
        }
        // 登入成功 返回jwt令牌
        Map<String, Object> claims = new TreeMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        claims.put(JwtClaimsConstant.USER_NAME, user.getUsername());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        return Result.success(loginVo);
    }

    @AutoFill
    @Override
    public Result<LoginVo> register(UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            return Result.fail(MessageConstant.REGISTER_FAIL); // 注册失败
        }
        if (userRegisterDto.getName() == null || userRegisterDto.getName().length() > 18) {
            return Result.fail(MessageConstant.NAME_ILLEGAL); // 用户名非法
        }
        // 校验密码
        if (userRegisterDto.getPassword() == null || userRegisterDto.getPassword().length() < 6 || userRegisterDto.getPassword().length() > 16) {
            return Result.fail(MessageConstant.USER_PASSWORD_ERROR); // 密码非法
        }
        // 查询是否已经有该用户
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, userRegisterDto.getName());
        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (user != null) {
            return Result.fail(MessageConstant.USER_EXIST); // 用户已存在
        }
        user = User.builder()
                .password(PasswordUtil.encryptPassword(userRegisterDto.getPassword()))
                .gender(userRegisterDto.getGender())
                .phone(userRegisterDto.getPhone())
                .username(userRegisterDto.getName())
                .createdTime(userRegisterDto.getCreatedTime())
                .build();
        userMapper.insert(user);
        log.info("user:{}", user);
        // 注册成功 返回token
        Map<String, Object> claims = new TreeMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        return Result.success(new LoginVo(token));
    }



    @Override
    public Result<String> update(UserUpdateDto userUpdateDto) {
        // 获取当前登入用户id
        Long userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        user.setPhone(userUpdateDto.getPhone() == null || userUpdateDto.getPhone().isEmpty() ? user.getPhone() : userUpdateDto.getPhone());
        user.setGender(userUpdateDto.getGender() == null || userUpdateDto.getGender().isEmpty() ? user.getGender() : userUpdateDto.getGender());
        user.setPassword(userUpdateDto.getPassword() == null || userUpdateDto.getPassword().isEmpty() ? user.getPassword() : PasswordUtil.encryptPassword(userUpdateDto.getPassword()));
        user.setUsername(userUpdateDto.getName() == null || userUpdateDto.getName().isEmpty() ? user.getUsername() : userUpdateDto.getName());
        userMapper.updateById(user);
        return Result.success();
    }

    @Override
    public Result<String> delete() {
        Long userId = getCurrentUserId();
        userMapper.deleteById(userId);
        return Result.success();
    }

    // 获取当前登录用户id
    private Long getCurrentUserId() {
        String token = threadLocal.get();
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
        Long userId = claims.get(JwtClaimsConstant.USER_ID, Long.class);
        log.info("用户id: {}", userId);
        return userId;
    }
}
