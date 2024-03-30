package org.example.quantumcommunity.exception;
import lombok.Getter;

/**
 * @author xiaol
 * @description: 全局异常处理器
 */
@Getter
public class GlobalException extends RuntimeException{
    private final int statusCode;

    public GlobalException(String message,int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
