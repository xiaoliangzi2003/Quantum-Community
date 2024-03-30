package org.example.quantumcommunity.service;

/**
 * @author xiaol
 */
public interface LikeService {
    /**
     * 判断Redis中用户是否已经点赞
     * @param: username 执行点赞操作的用户名
     * @param: id 文章或评论的id
     * @param: type 0-文章，1-评论
     * @return: boolean 是否已经点赞
     */
    boolean hasLikedInRedis(String username, Integer id,Integer type);

    /**
     * 判断数据库中用户是否已经点赞
     * @param: username 执行点赞操作的用户名
     * @param: id 文章或评论的id
     * @param: type 0-文章，1-评论
     * @return: boolean 是否已经点赞
     */
    boolean hasLikedInDatabase(String username, Integer id, Integer type);

    /**
     * 判断Redis中用户是否已经取消点赞
     * @param: username
     * @param: articleId
     * @return: boolean
     */
    boolean hasUnlikedInRedis(String username, Integer articleId, Integer type);

    /**
     * @Description: 添加点赞记录到Redis
     * @param: username 用户名
     * @param: id 文章或评论的id
     * @param: type 0-文章，1-评论
     * @return: void
     * */
    void addLikeInRedis(String username, Integer id, Integer type);

    /**
     * @Description: 添加点赞记录到数据库
     * @param: username 用户名
     * @param: id 文章或评论的id
     * @param: type 0-文章，1-评论
     * @return: void
     * */
    void addLikeInDatabase(String username, Integer id, Integer type);

    /**
     * @Description: 添加取消点赞记录到Redis
     * @param: username 用户名
     * @param: articleId 文章id
     * @param: type 0-文章，1-评论
     * @return: void
     * */

    void addUnlikeInRedis(String username, Integer articleId, Integer type);

    /**
     * @Description: 移除Redis中的点赞记录
     * @param: username 用户名
     * @param: articleId 文章id
     * @param: type 0-文章，1-评论
     * @return: void
     * */
    void removeLikeInRedis(String username, Integer articleId, Integer type);

    /**
     * @Description: 移除数据库中的点赞记录
     * @param: username 用户名
     * @param: articleId 文章id
     * @param: type 0-文章，1-评论
     * @return: void
     * */
    void removeLikeInDatabase(String username, Integer articleId, Integer type);

    /**
     * @Description: 从数据库中获取点赞数
     * @param: id 文章或评论的id
     * @param: type 0-文章，1-评论
     * */
    long getLikeCountFromDb(Integer id, Integer type);

    void incrementLikeCountInRedis(Integer id, Integer type);

    void decrementLikeCountInRedis(Integer id, Integer type);
}
