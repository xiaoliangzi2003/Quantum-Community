package org.example.quantumcommunity.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.quantumcommunity.model.User;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author xiaol
 *
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(Integer id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User getUserByUsername(String username);

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User getUserByUsernameAndPassword(String username,String password);

    @Select("SELECT * FROM user WHERE email = #{email} AND password = #{password}")
    User getUserByEmailAndPassword(String email, String password);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User getUserByEmail(String email);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User getUserByPhone(String phone);

    @Select("SELECT * FROM user WHERE username = #{username} AND email = #{email}")
    User getUserByUsernameAndEmail(String username,String email);

    @Select("INSERT INTO user (id, username, password,email,phone) VALUES (#{id}, #{username}, #{password},#{email},#{phone})")
    void insertUser(User user);

    @Select("UPDATE user SET username = #{username}, password = #{password} WHERE id = #{id}")
    void updateUser(User user);

    @Select("DELETE FROM user WHERE id = #{id}")
    void deleteUser(Integer id);

    @Select("SELECT * FROM user WHERE phone = #{phone} AND password = #{password}")
    User getUserByPhoneAndPassword(String phone, String password);

    @Select("UPDATE user SET email = #{newEmail} WHERE username = #{username} AND password = #{password}")
    void updateEmail(String username, String password, String newEmail);

    @Select("UPDATE user SET phone = #{newPhone} WHERE username = #{username} AND password = #{password}")
    void updatePhone(String username, String password, String newPhone);

    @Select("UPDATE user SET password = #{newPassword} WHERE username = #{username} AND password = #{oldPassword}")
    void updatePassword(String username, String oldPassword, String newPassword);

    @Select("SELECT * FROM user")
    List<User> getAllUser();

    @Select("UPDATE user SET status = #{status} WHERE username = #{username}")
    void updateStatus(String username, String status);

    @Select("SELECT status FROM user WHERE username = #{username}")
    String getStatus(String username);

    @Select("UPDATE user SET lastLoginTime = #{lastLoginTime} WHERE username = #{username}")
    void updateLoginTime(String username, Timestamp lastLoginTime);

    @Select("SELECT password FROM user WHERE username = #{username}")
    String getPasswordByUsername(String username);

    @Select("SELECT password FROM user WHERE email = #{email}")
    String getPasswordByEmail(String email);

    @Select("SELECT password FROM user WHERE phone = #{phone}")
    String getPasswordByPhone(String phone);

    @Select("SELECT username FROM user WHERE email = #{email}")
    String getUsernameByEmail(String email);

    @Select("SELECT username FROM user WHERE phone = #{phone}")
    String getUsernameByPhone(String phone);

    @Select("SELECT avatarUrl FROM user WHERE username = #{username}")
    String getAvatarByUsername(String username);

    @Select("UPDATE user SET avatarUrl = #{avatarUrl} WHERE username = #{username}")
    void updateAvatar(String username, String avatarUrl);
}
