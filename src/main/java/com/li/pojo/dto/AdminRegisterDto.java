package com.li.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminRegisterDto {
    private String name;
    private String phone;
    private String password;
    private LocalDateTime createdTime;
}
