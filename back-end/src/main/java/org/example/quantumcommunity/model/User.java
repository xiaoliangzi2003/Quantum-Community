package org.example.quantumcommunity.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quantumcommunity.annotation.NotNull;
import org.example.quantumcommunity.annotation.RandomId;

/**
 * @author xiaol
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @RandomId
    private Integer id;
    @NotNull
    private String username;
    @NotNull
    private String password;
    private String email;
    private String phone;
    private String status;
    private String createTime;
    private String lastLoginTime;
    private String type;
    private String wechatCode;
    private String githubCode;
}
