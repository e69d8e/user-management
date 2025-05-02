package com.li.server.service;

import com.li.common.result.Result;
import com.li.pojo.dto.LoginDto;
import com.li.pojo.dto.UserRegisterDto;
import com.li.pojo.dto.UserUpdateDto;
import com.li.pojo.vo.LoginVo;

public interface UserService {
    Result<LoginVo> login(LoginDto loginDto);

    Result<LoginVo> register(UserRegisterDto userRegisterDto);

    Result<String> update(UserUpdateDto userUpdateDto);

    Result<String> delete();
}
