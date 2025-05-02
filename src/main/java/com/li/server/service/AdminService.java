package com.li.server.service;

import com.li.common.result.PageResult;
import com.li.common.result.Result;
import com.li.pojo.dto.AdminRegisterDto;
import com.li.pojo.dto.LoginDto;
import com.li.pojo.entity.User;
import com.li.pojo.vo.LoginVo;

public interface AdminService {
    Result<LoginVo> login(LoginDto loginDto);

    Result<LoginVo> register(AdminRegisterDto registerDto);

    Result<String> updateStatus(Long id, Integer status);

    Result<PageResult<User>> page(Integer page, Integer pageSize, String name);

    Result<String> delete(Long id);
}
