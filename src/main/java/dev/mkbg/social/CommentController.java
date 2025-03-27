package dev.mkbg.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
//    @Autowired
//    private CommentService commentService;

//    //Just for postman.
//    @PostMapping
//    public ResponseEntity<Comment> createComment(@RequestBody Map<String , String > payload){
//        return new ResponseEntity<Comment>(commentService.createComment(payload.get("content"), payload.get("postId")), HttpStatus.CREATED);
//    }
}
