package com.li.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("user")
public class User {
    private Long id;
    private String username;
    private String phone;
    private LocalDateTime createdTime;
    private Integer status;
    private String password;
    private String gender;
}
