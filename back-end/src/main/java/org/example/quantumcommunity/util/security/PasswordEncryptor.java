package org.example.quantumcommunity.util.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密器：接收原始密码和随机的盐值进行组合，对组合后的字符串进行多次哈希
 * 最后将盐值和哈希后的密码拼接成一个字符串返回
 * @author xiaol
 */
public class PasswordEncryptor {
    // 生成随机盐
    private static final SecureRandom secureRandom = new SecureRandom();
    // 盐的长度
    private static final int SALT_LENGTH = 16;
    // 迭代次数
    private static final int ITERATIONS = 5;
    public static String encrypt(String rawPassword) {
        // 生成随机盐
        byte[] salt= new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        // 将随机盐转换为Base64字符串
        String saltBase64= Base64.getEncoder().encodeToString(salt);

        // 将密码和盐拼接
        String hashed=rawPassword;
        for (int i = 0; i < ITERATIONS; i++) {
            hashed = hash(hashed + saltBase64);
        }
        return saltBase64 + "$" +hashed;
    }

    // 将字符串进行SHA-256哈希
    private static String hash(String input){
        try{
            // 创建MessageDigest对象
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            // 将字符串转换为字节数组
            byte[] encodedhash=digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // 将字节数组转换为十六进制字符串
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /*
     * 解码函数：验证密码是否正确
     * 接收原始密码和存储的哈希值，执行相同的哈希算法，将结果与存储的哈希值进行比较
     * */
    public static boolean verify(String rawPassword, String storedPassword) {
        String[] saltAndHash = storedPassword.split("\\$");
        String saltBase64 = saltAndHash[0];
        String storedHash = saltAndHash[1];
        String hashed = rawPassword;
        for (int i = 0; i < ITERATIONS; i++) {
            hashed = hash(hashed + saltBase64);
        }
        return hashed.equals(storedHash);
    }
}
