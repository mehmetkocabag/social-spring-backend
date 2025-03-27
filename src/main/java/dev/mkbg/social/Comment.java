package dev.mkbg.social;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private ObjectId commentId;
    private ObjectId postId;
    private ObjectId authorId;
    private String content;

    public Comment(String content) {
        this.content = content;
    }
}


