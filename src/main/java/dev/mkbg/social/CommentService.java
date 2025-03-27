package dev.mkbg.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
//    @Autowired
//    private CommentRepository commentRepository;
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//
//    //Just for postman.
//    public Comment createComment(String content, String postId){
//       Comment comment = commentRepository.insert(new Comment(content));
//
//       mongoTemplate.update(Comment.class)
//               .matching(Criteria.where(postId).is(postId))
//               .apply(new Update().push("commentsIds").value(comment))
//               .first();
//       return comment;
//    }
}
