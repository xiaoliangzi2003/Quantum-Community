package org.example.quantumcommunity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author xiaol
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfile {
    String nickname;
    String username;
    String gender;
    Date birthday;
    String location;
    String signature;
    String wechat;
    String github;
    List<Integer> collections;
    List<Integer> history;
    List<Integer> followings;
    List<Integer> followers;
    List<Integer> articles;
    List<Integer> comments;
    List<Integer> likes;
    List<Integer> forwards;
}
