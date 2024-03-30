package org.example.quantumcommunity.util.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * @author xiaol
 */
@Service
public class LoginFailedUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void loginFailed(String username) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.increment("loginFailed:" + username, 1);
    }

    public String getFailedAttempts(String username) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        return ops.get("loginFailed:" + username);
    }
}
