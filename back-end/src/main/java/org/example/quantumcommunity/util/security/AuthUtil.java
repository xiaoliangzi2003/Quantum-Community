package org.example.quantumcommunity.util.security;

import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaol
 */
@Service
public class AuthUtil {
    @Autowired
    private UserMapper userMapper;

    //将AuthRequest对象中的account字段解析为username
    public String authRequestGetUsername(String account) {
        String username = null;
        //如果包含@，说明是邮箱
        if (account.contains("@")) {
            username = userMapper.getUsernameByEmail(account);
        }else if (account.matches(".*[a-zA-Z].*")) {
            username = account;
        }else if (account.matches("^[0-9]*$") && account.length() == 11) {
            username = userMapper.getUsernameByPhone(account);
        }else {
            throw new GlobalException("非法请求", Result.NOT_FOUND);
        }
        return username;
    }

}
