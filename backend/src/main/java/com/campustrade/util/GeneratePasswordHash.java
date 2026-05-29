package com.campustrade.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 运行此 main 方法生成 BCrypt 密码哈希，用于 init.sql 中的管理员初始密码。
 * 在 IDE 中右键运行，或通过 Maven: mvn exec:java
 */
public class GeneratePasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        System.out.println("BCrypt hash for 'admin123':");
        System.out.println(hash);
    }
}
