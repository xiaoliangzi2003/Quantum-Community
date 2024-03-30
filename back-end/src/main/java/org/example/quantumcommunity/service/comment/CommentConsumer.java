package org.example.quantumcommunity.service.comment;

import org.example.quantumcommunity.model.Comment;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentConsumer {
    @Autowired
    private CommentService commentService;

    @RabbitListener(queues = "commentQueue1")
    public void receiveComment1(Comment comment) {
        commentService.addComment(comment);
    }

    @RabbitListener(queues = "commentQueue2")
    public void receiveComment2(Comment comment) {
        commentService.addComment(comment);
    }

    @RabbitListener(queues = "commentQueue3")
    public void receiveComment3(Comment comment) {
        commentService.addComment(comment);
    }
}
