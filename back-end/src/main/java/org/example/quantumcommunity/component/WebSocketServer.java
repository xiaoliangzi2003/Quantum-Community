package org.example.quantumcommunity.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.model.Comment;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaol
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{username}")
public class WebSocketServer {
    //连接数量：0表示无限制
    private static int onlineCount = 0;
    //利用ConcurrentHashMap存放每个客户端对应的WebSocket对象
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    //与客户端的会话
    private Session session;
    //会话的用户名
    private String username;
    //连接建立时调用
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username){
        this.session = session;
        this.username=username;
        log.info("当前连接的用户名为：" + username);
        //将客户端的WebSocket对象存入webSocketMap
        webSocketMap.put(username, this);
        //在线人数加1
        addOnlineCount();
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(username);
        subOnlineCount();
        log.info(username + "断开连接！当前在线人数为" + getOnlineCount());
    }

    //发送评论信息到客户端
    public void sendCommentToUser(Comment comment, String username) {
        WebSocketServer webSocketServer = webSocketMap.get(username);
        if (webSocketServer != null) {
            try {
                Map<String,Object> commentMap = new ConcurrentHashMap<>();
                commentMap.put("id",comment.getId());
                commentMap.put("content",comment.getContent());
                commentMap.put("commentator",comment.getCommentator());
                commentMap.put("likes",comment.getLikes());
                commentMap.put("createTime",comment.getCreateTime());
                commentMap.put("parentCommentId",comment.getParentCommentId());
                commentMap.put("repliedCommentId",comment.getRepliedCommentId());
                commentMap.put("repliedCommentator",comment.getRepliedCommentator()==null?"":comment.getRepliedCommentator());
                commentMap.put("articleId",comment.getArticleId());
                commentMap.put("repliedContent",comment.getRepliedContent()==null?"":comment.getRepliedContent());
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(commentMap);
                webSocketServer.session.getBasicRemote().sendText(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //发送消息给指定用户
    public void sendInfo(String message, String receiver) {
        WebSocketServer webSocketServer = webSocketMap.get(receiver);
        try {
            webSocketServer.session.getBasicRemote().sendText(message);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }


}
