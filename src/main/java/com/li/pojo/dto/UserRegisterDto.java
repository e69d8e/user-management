package com.li.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRegisterDto {
    private String name;
    private String phone;
    private String password;
    private String gender;
    private LocalDateTime createdTime;
}
