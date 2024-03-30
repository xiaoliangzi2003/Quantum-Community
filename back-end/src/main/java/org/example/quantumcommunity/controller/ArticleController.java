package org.example.quantumcommunity.controller;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.service.file.FileService;
import org.example.quantumcommunity.util.other.TimeUtil;
import org.example.quantumcommunity.util.security.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

import org.example.quantumcommunity.mapper.ArticleMapper;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.model.Article;
import org.example.quantumcommunity.model.User;
import org.example.quantumcommunity.service.article.ArticleService;


/**
 * @author xiaol
 */
@Slf4j
@RestController
@RequestMapping("/blog")
public class ArticleController {

    @Autowired
    private ArticleService articleSerivce;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    private FileService fileService;

    TimeUtil timeUtil=new TimeUtil();

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;


    /**
     * @param articleMap 文章对象
     * @return Result:上传结果
     * @description: 上传文章
     * @path: /blog/publish-article
     * @method: POST
     * */
    @PostMapping("/publish-article")
    public Result publishArticle(@RequestBody Map<String,Object> articleMap){
        try{
            Article article=articleSerivce.publishAritcle(articleMap);
            log.info("发布成功");
            return new Result(Result.OK,"发布成功",article);
        }catch (Exception e){
            log.info("发布失败");
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param title 文章标题
     * @return Result:获取结果
     * @description: 根据标题获取文章
     * @path: /blog/article-title/{title}
     * @method: GET
     * @throw Exception GlobalException
     * */
    @GetMapping("/article-title/{title}")
    public Result getArticleByTitle(@PathVariable("title") String title){
        try{
            Article article= articleSerivce.getArticleByTitle(title);
            return new Result(Result.OK,"获取文章成功",article);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param article 文章对象
     * @return Result:更新结果
     * @description: 更新文章
     * @path: /blog/update-article
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article")
    public Result updateArticle(@RequestBody Article article){
        try{
            articleSerivce.updateArticle(article);
            return new Result(Result.OK,"更新成功",article);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @return Result:删除结果
     * @description: 删除文章
     * @path: /blog/delete-article/{id}
     * @method: DELETE
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-views/{id}")
    public Result updateArticleViews(@PathVariable("id") int id){
        try{
            articleSerivce.updateArticleViews(id);
            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param request 包含flag
     * @return Result:更新结果
     * @description: 更新文章的点赞量
     * @path: /blog/update-article-likes/{id}
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-likes")
    public Result updateArticleLikes(@RequestBody Map<String,Object> request){
        try{
            String username=(String) request.get("username");
            int id=(int) request.get("id");
            int flag=(int) request.get("flag");
            //更新点赞
            articleSerivce.updateArticleLikes(id,flag,username);

            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @param flag 标志位 0表示评论量-1 1表示评论量+1
     * @return Result:更新结果
     * @description: 更新文章的评论量
     * @path: /blog/update-article-comments/{id}
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-comments/{id}")
    public Result updateArticleComments(@PathVariable("id") int id,@RequestParam int flag){
        try{
            articleSerivce.updateArticleComments(id,flag);
            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @param flag 标志位 0表示收藏量-1 1表示收藏量+1
     * @return Result:更新结果
     * @description: 更新文章的收藏量
     * @path: /blog/update-article-collects/{id}
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-collects/{id}")
    public Result updateArticleCollects(@PathVariable("id") int id,@RequestParam int flag){
        try{
            articleSerivce.updateArticleCollects(id,flag);
            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param id 文章id
     * @return Result:更新结果
     * @description: 更新文章的转发量
     * @path: /blog/update-article-forwards/{id}
     * @method: PUT
     * @throw Exception GlobalException
     * */
    @PutMapping("/update-article-forwards/{id}")
    public Result updateArticleForwards(@PathVariable("id") int id){
        try{
            articleSerivce.updateArticleForwards(id);
            return new Result(Result.OK,"更新成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }



    /**
     * @param id 文章id
     * @return ModelAndView:文章详情
     * @description: 获取文章详情
     * @path: /blog/article-detail/{id}
     * @method: GET
     * @throw Exception GlobalException
     * */
    @GetMapping("/article-detail/{id}")
    public Map<String, Object> articleDetail(@PathVariable("id") int id){
        //返回的响应
        Map<String, Object> response = new HashMap<>();
        //获取文章
        Article article= articleSerivce.getArticleById(id);
        response.put("author", article.getAuthor());

        //获取文章创建时间
        Instant createInstant = Instant.ofEpochMilli(article.getCreateTimeStamp());
        LocalDateTime createLocalDateTime = LocalDateTime.ofInstant(createInstant, ZoneId.systemDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = createLocalDateTime.format(dateTimeFormatter);
        response.put("createTime", createTime);

        //获取文章修改时间
        if (article.getUpdateTimeStamp() != 0) {
            Instant updateInstant = Instant.ofEpochMilli(article.getUpdateTimeStamp());
            LocalDateTime updateLocalDateTime = LocalDateTime.ofInstant(updateInstant, ZoneId.systemDefault());
            String updateTime = updateLocalDateTime.format(dateTimeFormatter);
            response.put("updateTime", updateTime);
        }

        //获取文章在数据库中的标题以及文件夹名
        String dirName=article.getTitle();
        int index = dirName.indexOf('_');
        if (index != -1) {
            dirName = dirName.substring(index + 1);
        }
        //获取md文件路径
        String objectName = article.getAuthor() + "_" + dirName + ".md";
        //获取md文件内容
        String content=  fileService.getFileContent(objectName);
        response.put("content",content);
        return response;
    }

    /**
     * @param file 上传的文件
     * @param username 用户名
     * @return Result:上传结果
     * @description: 上传本地文件
     * @path: /blog/upload
     * @method: POST
     * @throw Exception GlobalException
     * */
    @PostMapping("/upload")
    public Result uploadLocalFile(@RequestParam("file") MultipartFile file,@RequestParam String username) throws IOException, InterruptedException {
        InputStream inputStream = file.getInputStream();
        String originFileName = file.getOriginalFilename();
        String fileNameWithOutSuffix = originFileName.substring(0, originFileName.lastIndexOf("."));
        //根据flag的值判断是在数据表中创建还是更新原来的文章 flag=0表示创建，flag=1表示更新
        int flag=0;

        //检查username是否存在
        if(userMapper.getUserByUsername(username)==null){
            return new Result(Result.ERROR,"用户不存在",null);
        }
        try {
            //创建文件在服务器端的存放路径
            String saveFilePath = "src/main/resources/static/articles/" + username + "/" +fileNameWithOutSuffix + "/";
            File uploadDir = new File(saveFilePath);

            //如果这个文件夹不存在则创建
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String theFileName=username+"_"+fileNameWithOutSuffix;
            //如果文件已经存在则删除
            File saveFile = new File(saveFilePath, theFileName+".md");
            //如果文件已存在并且数据库中可以查询到同名文章或文件不存在但是数据库中可查询到文章则更新文章
            if ((saveFile.exists() && articleSerivce.getArticleByTitle(theFileName)!=null)||
                    (!saveFile.exists() && articleSerivce.getArticleByTitle(theFileName)!=null)){
                saveFile.delete();
                flag=1;
            }
            //文件名：username_title.md 这样可以避免不同用户上传同一个文件导致的冲突
            FileCopyUtils.copy(inputStream, new FileOutputStream(saveFilePath +theFileName+ ".md"));

            //创建文章对象
            if(flag==0){
                Article article = new Article();
                article.setTitle(theFileName);
                article.setAuthor(username);
                articleSerivce.uploadArticle(article);
                log.info("上传文章成功");
            }else{
                Article article= articleSerivce.getArticleByTitle(theFileName);
                article.setUpdateTimeStamp(System.currentTimeMillis());
                articleSerivce.updateArticle(article);
                log.info("更新文章成功");
            }
        } catch (IOException e) {
            throw new GlobalException("上传失败", Result.UPLOAD_FAILED);
        }
        return new Result(Result.OK, "上传成功", null);
    }

    /**
     * @param id 文章id
     * @return Result:删除结果
     * @description: 删除文章
     * @path: /blog/delete-article/{id}
     * @method: DELETE
     * @throw Exception GlobalException
     * */
    @DeleteMapping("/delete-article/{id}")
    public Result deleteArticle(@PathVariable("id") int id){
        try{
            articleSerivce.deleteArticleById(id);
            log.info("删除文章成功");
            return new Result(Result.OK,"删除成功",null);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param articleId 文章id
     * @return Result:获取结果
     * @description: 获取文章简要信息
     * @path: /blog/get-article-brief-info/{articleId}
     * @method: GET
     * */
    @GetMapping("/get-article-brief-info/{articleId}")
    public Result getArticleBriefInfo(@PathVariable("articleId") String articleId){
        try{
            int id=Integer.parseInt(articleId);
            Article article=articleSerivce.getArticleById(id);
            String title=article.getTitle();
            String author=article.getAuthor();
            String publishTime=timeUtil.timeStampToString(article.getCreateTimeStamp());
            String views=String.valueOf(article.getViews());
            String likes=String.valueOf(article.getLikes());
            String comments=String.valueOf(article.getComments());
            String collects=String.valueOf(article.getCollects());
            String shares=String.valueOf(article.getForwards());
            Map<String,String> response=new HashMap<>();
            response.put("title",title);
            response.put("author",author);
            response.put("publishTime",publishTime);
            response.put("views",views);
            response.put("likes",likes);
            response.put("comments",comments);
            response.put("collects",collects);
            response.put("shares",shares);
            return new Result(Result.OK,"获取文章简要信息成功",response);
        }catch (Exception e){
            return new Result(Result.ERROR,e.getMessage(),null);
        }
    }

    /**
     * @param map 包含pageNum和pageSize
     * @return Result:获取结果
     * @description: 获取文章列表
     * @path: /blog/article-list
     * @method: POST
     * */
    @PostMapping("/article-list")
    public Result getArticleList(@RequestBody Map<String,Object> map){
        try{
            int pageNum=(int) map.get("pageNum");
            int pageSize = (int) map.get("pageSize");
            List<Article> articleList = articleSerivce.getArticleListByPage(pageNum, pageSize);
            long total=articleMapper.getTotalArticles();
            Map<String,Object> response=new HashMap<>();
            response.put("articleList",articleList);
            response.put("total",total);
            return new Result(Result.OK, "获取文章列表成功", response);
        } catch (Exception e) {
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    /**
     * @param map 包含author、pageNum和pageSize
     * @return Result:获取结果
     * @description: 获取作者的文章列表
     * @path: /blog/article-list-by-author
     * @method: POST
     * */
    @PostMapping("/article-list-by-author")
    public Result getArticleListByAuthor(@RequestBody Map<String,Object> map){
        try{
            String author=(String) map.get("author");
            int pageNum=(int) map.get("pageNum");
            int pageSize = (int) map.get("pageSize");
            List<Article> articleList = articleSerivce.getArticleListByAuthor(author,pageNum, pageSize);
            long total=articleMapper.getTotalArticlesByAuthor(author);
            Map<String,Object> response=new HashMap<>();
            response.put("articleList",articleList);
            response.put("total",total);
            return new Result(Result.OK, "获取文章列表成功", response);
        } catch (Exception e) {
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/article-list-by-category")
    public Result getArticleByCategory(@RequestBody Map<String,Object> request){
        try{
            String category=(String) request.get("category");
            int pageNum=(int) request.get("pageNum");
            int pageSize = (int) request.get("pageSize");
            List<Article> articleList = articleSerivce.getArticleListByCategory(category,pageNum, pageSize);
            Map<String,Object> response=new HashMap<>();
            response.put("articleList",articleList);
            return new Result(Result.OK, "获取文章列表成功", response);
        } catch (Exception e) {
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/upload-image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            //随机生成文件名
            String fileName = System.currentTimeMillis() + originalFilename;
            //调用fileService的uploadImage方法上传图片
            fileService.uploadFile(fileName, file.getInputStream());
            //获取图片的URL
            String fileUrl = "https://quantumblog.oss-cn-chengdu.aliyuncs.com/"+fileName;
            // 创建一个包含文件URL的Result对象
            return new Result(Result.OK, "上传成功", fileUrl);
        } catch (Exception e) {
            // 如果出现错误，返回一个包含错误信息的Result对象
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }
}
