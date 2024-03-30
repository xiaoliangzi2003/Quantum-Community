package org.example.quantumcommunity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

/**
 * @author xiaol
 * @Description: 点赞记录
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRecord {
    @Id
    private Long id;
    //用户Id
    private Long userId;
    //文章或评论Id
    private Long articleOrCommentId;
    //点赞时间
    private Long instrumentTime;
    //点赞类型：0-文章，1-评论
    private Integer type;
    @Version
    //版本号：用于乐观锁，如果版本号不一致，说明数据已经被修改，则抛出异常
    private Integer version;
}
