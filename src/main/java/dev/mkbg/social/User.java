package dev.mkbg.social;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private ObjectId userId;

    private String username;
    private String password;
    private String email;
    private String profilePicture;

    private List<ObjectId> publishedPostIds = new ArrayList<>();
    private List<ObjectId> likedPostIds = new ArrayList<>();
    private List<ObjectId> dislikedPostIds = new ArrayList<>();
    private List<ObjectId> publishedCommentIds = new ArrayList<>();


    void addPublishedPost(Post post) {
        publishedPostIds.add(post.getPostId());
    }
    void addLikedPost(Post post) {
        if (likedPostIds.contains(post.getPostId())) {
            likedPostIds.remove(post.getPostId());
            return;
        }
        likedPostIds.add(post.getPostId());
        if (dislikedPostIds.contains(post.getPostId())) {
            dislikedPostIds.remove(post.getPostId());
        }
    }
    void addDislikedPost(Post post) {
        if (dislikedPostIds.contains(post.getPostId())) {
            dislikedPostIds.remove(post.getPostId());
            return;
        }
        dislikedPostIds.add(post.getPostId());
        if (likedPostIds.contains(post.getPostId())) {
            likedPostIds.remove(post.getPostId());
        }
    }

    void addComment(Comment comment) {
        publishedCommentIds.add(comment.getCommentId());
    }

    public List<ObjectId> getPublishedPosts() {
        return publishedPostIds;
    }
    public List<ObjectId> getLikedPosts() {
        return likedPostIds;
    }
    public List<ObjectId> getDislikedPosts() {
        return dislikedPostIds;
    }

    public String getPublishedPostsCount() {
        return String.valueOf(publishedPostIds.size());
    }
    public String getLikedPostsCount() {
        return String.valueOf(likedPostIds.size());
    }
    public String getDislikedPostsCount() {
        return String.valueOf(dislikedPostIds.size());
    }
    public String getPublishedCommentsCount() {
        return String.valueOf(publishedCommentIds.size());
    }

//    void removePublishedPost() {}
//    void removeLikedPost() {}
//    void removeDislikedPost() {}
//    void removePublishedComment() {}
}