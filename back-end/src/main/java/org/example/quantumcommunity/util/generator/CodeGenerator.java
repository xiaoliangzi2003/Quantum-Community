package org.example.quantumcommunity.util.generator;

public class CodeGenerator {
    //生成随机验证码
    public static String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }
}
