package org.example.quantumcommunity.service.rules;

import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.util.security.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaol
 */
@Service
public class RulesServiceImpl implements RulesService {
    @Autowired
    UserMapper userMapper;
    @Override
    public void passwordRule(String password) {
        Pattern pattern= Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$");
        Matcher matcher=pattern.matcher(password);
        if(!matcher.matches()){
            throw new GlobalException("密码必须包含字母、数字和符号", Result.PASSWORD_INVALID);
        }
        if(password.length()<6 || password.length()>15){
            throw new GlobalException("密码的长度必须在6到15位之间",Result.PASSWORD_INVALID);
        }
    }

    @Override
    public void usernameRule(String username) {

    }

    @Override
    public void phoneRule(String phone) {

    }

    @Override
    public void emailRule(String email) {

    }

    @Override
    public void checkExistRule(String username, String email, String phone, String type) {

    }

    @Override
    public void checkIsRightPassword(String username, String password) {

    }

    @Override
    public boolean checkAtLeastOneBinding(String username) {
        return false;
    }
}
