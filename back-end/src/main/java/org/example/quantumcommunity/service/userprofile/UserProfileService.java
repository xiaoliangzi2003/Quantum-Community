package org.example.quantumcommunity.service.userprofile;

import org.example.quantumcommunity.model.UserProfile;
import org.springframework.stereotype.Service;

/**
 * @author xiaol
 */
@Service
public interface UserProfileService {

    UserProfile getUserProfileByUsername(String username);

    void updateUserProfile(UserProfile userProfile);

}
