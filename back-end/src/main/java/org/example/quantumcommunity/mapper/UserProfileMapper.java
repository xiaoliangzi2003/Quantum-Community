package org.example.quantumcommunity.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.quantumcommunity.model.UserProfile;

/**
 * @author xiaol
 */
@Mapper
public interface UserProfileMapper {
    @Select("insert into user_profile(username) values(#{username})")
    void insertUsername(String username);
    @Select("update user_profile set avatar=#{url} where username=#{username}")
    void changeAvatar(String username, String url);

    @Select("select * from user_profile where username=#{username}")
    UserProfile getUserProfileByUsername(String username);

    @Update("update user_profile " +
            "set nickname=#{nickname}, " +
            "gender=#{gender}," +
            "birthday=#{birthday} ," +
            "signature=#{signature}, "+
            "wechat=#{wechat}, "+
            "github=#{github}, "+
            " location=#{location} " +
            "where username=#{username}")
    void updateUserProfile(UserProfile userProfile);
}

