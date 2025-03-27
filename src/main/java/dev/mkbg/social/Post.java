package dev.mkbg.social;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    private ObjectId postId;
    private String title;
    private String content;
    private String picture;

    private ObjectId authorId;
    private List<ObjectId> commentIds = new ArrayList<>();
    private List<ObjectId> likedUserIds = new ArrayList<>();
    private List<ObjectId> dislikedUserIds = new ArrayList<>();

    private String likesCount = "0";
    private String dislikesCount = "0";
    private String commentsCount = "0";

    void like(User user) {
        ObjectId userId = user.getUserId();
        if (!likedUserIds.contains(userId)) {
            likedUserIds.add(userId);
            likesCount = String.valueOf(likedUserIds.size());
            if (dislikedUserIds.contains(userId)) {
                dislikedUserIds.remove(userId);
                dislikesCount = String.valueOf(dislikedUserIds.size());
            }
        }
        else {
            likedUserIds.remove(userId);
            likesCount = String.valueOf(likedUserIds.size());
        }
    }

    void dislike(User user) {
        ObjectId userId = user.getUserId();
        if (!dislikedUserIds.contains(userId)) {
            dislikedUserIds.add(userId);
            dislikesCount = String.valueOf(dislikedUserIds.size());
            if (likedUserIds.contains(userId)){
                likedUserIds.remove(userId);
                likesCount = String.valueOf(likedUserIds.size());
            }
        }
        else {
            dislikedUserIds.remove(userId);
            dislikesCount = String.valueOf(dislikedUserIds.size());
        }
    }

    void addComment(Comment comment) {
        commentIds.add(comment.getCommentId());
        commentsCount = String.valueOf(commentIds.size());
    }
}
