package com.li.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 加密密码
    public static String encryptPassword(String password) {
        return encoder.encode(password);
    }

    // 验证密码 rawPassword: 明文密码 encodedPassword: 密文密码
    public static boolean checkPassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        String rawPassword = "123456";
        String encodedPassword = encryptPassword(rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);

        boolean isMatch = checkPassword(rawPassword, encodedPassword);
        System.out.println("Password Match: " + isMatch);
    } // 测试用 $2a$10$PmyW9KEwh4QCUO72Aihx0ee4PnBdA/2zrZ1mqcFmJktXEvcnNY6nK
}
