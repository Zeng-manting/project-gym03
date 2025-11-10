package com.gym.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 * 提供密码加密功能
 */
public class PasswordUtil {

    // BCryptPasswordEncoder实例，使用单例模式
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 密码加密方法
     * 使用BCrypt算法对原始密码进行加密
     * 
     * @param raw 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return encoder.encode(raw);
    }

    /**
     * 密码匹配方法（可选添加）
     * 验证原始密码与加密密码是否匹配
     * 
     * @param raw 原始密码
     * @param encoded 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String raw, String encoded) {
        if (raw == null || encoded == null) {
            return false;
        }
        return encoder.matches(raw, encoded);
    }
}