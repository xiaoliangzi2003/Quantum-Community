package org.example.quantumcommunity.service.article;

import com.github.pagehelper.PageHelper;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.mapper.ArticleMapper;
import org.example.quantumcommunity.mapper.CommentMapper;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.model.Article;
import org.example.quantumcommunity.model.Comment;
import org.example.quantumcommunity.model.User;
import org.example.quantumcommunity.service.LikeService;
import org.example.quantumcommunity.service.file.FileService;
import org.example.quantumcommunity.util.other.TimeUtil;
import org.example.quantumcommunity.util.processor.RandomIdProcessor;
import org.example.quantumcommunity.util.security.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author xiaol
 */
@Service
@Slf4j
@EnableScheduling
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private FileService fileService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LikeService likeService;

    @Override
    public void uploadArticle(Article article) {
        try{
            //判断文章的属性
            if(article==null){
                throw new GlobalException("请求为空", Result.NULL_REQUEST);
            }
            if(StringUtils.isBlank(article.getTitle())){
                throw new GlobalException("标题为空",Result.NULL_TITLE);
            }
            if(article.getTitle().length()>50){
                throw new GlobalException("标题过长",Result.TOO_LONG_TITLE);
            }
            if(StringUtils.isBlank(article.getAuthor())){
                throw new GlobalException("请填写作者名",Result.NULL_AUTHOR);
            }
            article.setTop(false);
            article.setViews(0);
            article.setLikes(0);
            article.setComments(0);
            article.setCollects(0);
            article.setForwards(0);
            Date now=new Date();
            long nowStamp=now.getTime();
            article.setCreateTimeStamp(nowStamp);
            article.setUpdateTimeStamp(nowStamp);
            RandomIdProcessor.process(article);
            article.setStatus("publish");
            //如果没有标签，就默认为"未分类"
            if(StringUtils.isBlank(article.getTags())){
                article.setTags("未分类");
            }
            article.setUrl("/"+article.getId());
            articleMapper.addArticle(article);
        }catch (Exception e) {
            log.error("上传文章失败", e);
            throw new GlobalException("上传文章失败", Result.UPLOAD_FAILED);
        }
    }

    @Override
    public Article getArticleById(Integer id) {
        if(id==null){
            throw new GlobalException("请求为空",Result.NULL_REQUEST);
        }
        return articleMapper.getArticleById(id);
    }

    @Override
    public Article getArticleByTitle(String title) {
        if(StringUtils.isBlank(title)){
            return null;
        }
        Article article=articleMapper.getArticleByTitle(title);
        if(article==null){
            return null;
        }else {
            return article;
        }
    }

    @Override
    @Transactional
    public void updateArticle(Article article) {
        if(article==null){
            throw new GlobalException("请求为空",Result.NULL_REQUEST);
        }
        if(StringUtils.isBlank(article.getTitle())){
            throw new GlobalException("标题为空",Result.NULL_TITLE);
        }
        if(article.getTitle().length()>50){
            throw new GlobalException("标题过长",Result.TOO_LONG_TITLE);
        }
        if(StringUtils.isBlank(article.getAuthor())){
            throw new GlobalException("请填写作者名",Result.NULL_AUTHOR);
        }
        article.setUpdateTimeStamp(new Date().getTime());
        articleMapper.updateArticle(article);
    }

    @Override
    public List<Article> getArticleListByPage(int pageNum, int pageSize) {
        if(pageNum<1){
            throw new GlobalException("页数错误",Result.WRONG_PAGE_NUM);
        }
        if(pageSize<1){
            throw new GlobalException("每页数量错误",Result.WRONG_PAGE_SIZE);
        }
        PageHelper.startPage(pageNum,pageSize);
        return articleMapper.getArticleByPage((pageNum-1)*pageSize,pageSize);
    }

    @Override
    @Transactional
    //id：文章id
    public void deleteArticleById(Integer id) {
        if(id==null){
            throw new GlobalException("请求为空",Result.NULL_REQUEST);
        }else if(articleMapper.getArticleById(id)==null){
            throw new GlobalException("文章不存在",Result.NULL_ARTICLE);
        }

        //删除阿里云OSS上的文件
        String username=articleMapper.getArticleById(id).getAuthor();
        String title=articleMapper.getArticleById(id).getTitle();
        String fileName=username+"_"+title+".md";
        fileService.deleteFile(fileName);

        //删除文章
        articleMapper.deleteArticle(id);
        //删除文章的评论
        List<Comment> comments= commentMapper.getCommentListByArticleId(id);
        for(Comment comment:comments){
            commentMapper.deleteCommentById(comment.getId());
        }
    }

    @Override
    public void updateArticleViews(Integer id) {
        if(id==null){
            throw new GlobalException("请求为空",Result.NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        article.setViews(article.getViews()+1);
        articleMapper.updateArticle(article);
    }

    @Override
    public void updateArticleLikes(Integer id,int flag,String username) {
        if(id==null){
            throw new GlobalException("请求为空",Result.NULL_REQUEST);
        }
        if(flag==0){
            likeService.addLikeInRedis(username,id,0);
        }else{
            likeService.addUnlikeInRedis(username,id,0);
        }
    }

    @Override
    public void updateArticleComments(Integer id,int flag) {
        if (id == null) {
            throw new GlobalException("请求为空", Result.NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        if(flag==0) {
            article.setComments(article.getComments() + 1);
        }else{
            article.setComments(article.getComments() - 1);
        }
        articleMapper.updateArticle(article);
    }

    @Override
    public void updateArticleCollects(Integer id,int flag) {
        if (id == null) {
            throw new GlobalException("请求为空", Result.NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        if(flag==0) {
            article.setCollects(article.getCollects() + 1);
        }else{
            article.setCollects(article.getCollects() - 1);
        }
        articleMapper.updateArticle(article);
    }

    @Override
    public void updateArticleForwards(Integer id) {
        if (id == null) {
            throw new GlobalException("请求为空", Result.NULL_REQUEST);
        }
        Article article = articleMapper.getArticleById(id);
        article.setForwards(article.getForwards() + 1);
        articleMapper.updateArticle(article);
    }

    @Override
    public List<Article> getArticleListByAuthor(String author, int pageNum, int pageSize) {
        if(StringUtils.isBlank(author)){
            throw new GlobalException("作者为空",Result.NULL_AUTHOR);
        }
        if(pageNum<1){
            throw new GlobalException("页数错误",Result.WRONG_PAGE_NUM);
        }
        if(pageSize<1){
            throw new GlobalException("每页数量错误",Result.WRONG_PAGE_SIZE);
        }
        PageHelper.startPage(pageNum,pageSize);
        return articleMapper.getArticleListByAuthor(author,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public Article getArticleByTitleAndAuthor(String title, String author) {
        return articleMapper.getArticleByTitleAndAuthor(title,author);
    }

    @Override
    public List<Article> getArticleListByCategory(String category, int pageNum, int pageSize) {
        if(StringUtils.isBlank(category)){
            throw new GlobalException("分类为空",Result.NULL_CATEGORY);
        }
        if(pageNum<1){
            throw new GlobalException("页数错误",Result.WRONG_PAGE_NUM);
        }
        if(pageSize<1){
            throw new GlobalException("每页数量错误",Result.WRONG_PAGE_SIZE);
        }
        PageHelper.startPage(pageNum,pageSize);
        return articleMapper.getArticleListByCategory(category,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public Article publishAritcle(Map<String, Object> articleMap){
        try{
            Article article=new Article();
            String title=(String) articleMap.get("title");

            String content = (String) articleMap.get("content");
            String author=(String) articleMap.get("author");
            String tags=(String) articleMap.get("tags");

            article.setAuthor(author);
            article.setTitle(title);
            article.setTop(false);
            article.setViews(0);
            article.setLikes(0);
            article.setComments(0);
            article.setCollects(0);
            article.setForwards(0);
            article.setStatus("publish");
            article.setUrl("/"+article.getId());

            RandomIdProcessor.process(article);

            User user=userMapper.getUserByUsername(author);
            //用户名不存在
            if(user==null){
                return null;
            }

            //通过用户名获取用户id
            article.setAuthorId(user.getId());

            article.setTags(tags);
            log.info("tags:"+tags);
            article.setCategory((String) articleMap.get("category"));

            article.setVisibility((boolean) articleMap.get("isPublic"));

            article.setSummary((String) articleMap.get("summary"));

            String objectName = author + "_" + title + ".md";
            InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            //如果存在同名文件，删除
            if(fileService.isFileExist(objectName)){
                fileService.deleteFile(objectName);
            }
            fileService.uploadFile(objectName, inputStream);

            boolean isScheduled = (boolean) articleMap.get("isScheduled");
            if(isScheduled){
                String scheduleTime = (String) articleMap.get("scheduleTime");
                TimeUtil timeUtil = new TimeUtil();
                article.setCreateTimeStamp(timeUtil.stringToTimeStamp(scheduleTime));
                redisTemplate.opsForValue().set("ScheduledArticle:"+article.getTitle()+"_"+article.getAuthor(),article);
                return article;
            }
            if(articleMapper.getArticleByTitle(title)==null){
                article.setCreateTimeStamp(new Date().getTime());
                //写入数据库
                articleMapper.addArticle(article);
                return article;
            }else{
                if(articleMapper.getArticleByTitleAndAuthor(title,author)!=null){
                    article.setUpdateTimeStamp(new Date().getTime());
                    articleMapper.updateArticle(article);
                    return  article;
                }else{
                    return null;
                }
            }
        }catch (Exception e){
            throw new GlobalException("发布文章失败",Result.UPLOAD_FAILED);
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void publishScheduledArticles() {
        LocalDateTime currentTime = LocalDateTime.now();
        Set<String> keys = redisTemplate.keys("ScheduledArticle:*");
        log.info("Scheduled articles: " + (keys==null?"No scheduled articles":keys.size()));
        if(keys.size()==0){
            return;
        }
        try{
            for (String key : keys) {
                Article article = (Article) redisTemplate.opsForValue().get(key);
                RandomIdProcessor.process(article);
                article.setTop(false);
                article.setViews(0);
                article.setLikes(0);
                article.setComments(0);
                article.setCollects(0);
                article.setForwards(0);
                article.setUpdateTimeStamp(new Date().getTime());
                article.setStatus("publish");
                article.setUrl("/"+article.getId());
                assert article != null;
                TimeUtil timeUtil=new TimeUtil();
                LocalDateTime publishTime= timeUtil.timeStampToLocalDateTime(article.getCreateTimeStamp());
                if (publishTime.isBefore(currentTime) || publishTime.equals(currentTime)) {
                    articleMapper.addArticle(article);
                    redisTemplate.delete(key);
                    log.info("Article published: " + article.getTitle());
                }
            }
        }catch (Exception e){
            throw new GlobalException("发布定时文章失败",Result.UPLOAD_FAILED);
        }

    }

}
