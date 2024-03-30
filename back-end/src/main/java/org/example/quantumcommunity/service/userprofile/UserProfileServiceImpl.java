package org.example.quantumcommunity.service.userprofile;

import org.example.quantumcommunity.mapper.UserProfileMapper;
import org.example.quantumcommunity.model.UserProfile;
import org.example.quantumcommunity.util.other.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public UserProfile getUserProfileByUsername(String username) {
        try{
            return userProfileMapper.getUserProfileByUsername(username);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void updateUserProfile(UserProfile userProfile) {
        try{
            userProfileMapper.updateUserProfile(userProfile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
