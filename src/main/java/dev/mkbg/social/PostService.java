package dev.mkbg.social;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public User getPostAuthor(Post post) {
        return userRepository.findById(post.getAuthorId()).orElse(null);
    }

    public Optional<Post> getPostById(ObjectId id) {
        return postRepository.findById(id);
    }

    public void createPost(String userId, String title, String content) {
        ObjectId userObjectId = new ObjectId(userId);
        User user = userRepository.findById(userObjectId)
                .orElseThrow(() -> new RuntimeException("User not found/need login"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthorId(userObjectId);
        Post publishedPost = postRepository.save(post);

        user.addPublishedPost(publishedPost);
        userRepository.save(user);
    }

    public List<Post> searchPosts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                query, query);
    }

    public Post likePost(String postId, String userId) {
        ObjectId postObjectId = new ObjectId(postId);
        ObjectId userObjectId = new ObjectId(userId);

        Optional<Post> optionalPost = postRepository.findById(postObjectId);
        Optional<User> optionalUser = userRepository.findById(userObjectId);

        if (optionalPost.isPresent() && optionalUser.isPresent()) {
            Post post = optionalPost.get();
            User user = optionalUser.get();
            post.like(user);
            user.addLikedPost(post);
            userRepository.save(user);
            return postRepository.save(post);
        }
        return null;
    }

    public Post dislikePost(String postId, String userId) {
        ObjectId postObjectId = new ObjectId(postId);
        ObjectId userObjectId = new ObjectId(userId);

        Optional<Post> optionalPost = postRepository.findById(postObjectId);
        Optional<User> optionalUser = userRepository.findById(userObjectId);

        if (optionalPost.isPresent() && optionalUser.isPresent()) {
            Post post = optionalPost.get();
            User user = optionalUser.get();
            post.dislike(user);
            user.addDislikedPost(post);
            userRepository.save(user);
            return postRepository.save(post);
        }
        return null;
    }

    public List<Comment> getCommentsByPostId(String postId) {
        ObjectId postObjectId = new ObjectId(postId);
        Optional<Post> postOptional = getPostById(postObjectId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            return commentRepository.findAllByCommentIdIn(post.getCommentIds());
        }
        return Collections.emptyList();
    }

    public void addCommentToPost(String postId, String userId, String content) {
        ObjectId postObjectId = new ObjectId(postId);
        ObjectId userObjectId = new ObjectId(userId);

        Post post = postRepository.findById(postObjectId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userObjectId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPostId(postObjectId);
        comment.setAuthorId(userObjectId);

        Comment savedComment = commentRepository.save(comment);
        post.addComment(savedComment);
        user.addComment(savedComment);
        postRepository.save(post);
        userRepository.save(user);
    }
//        public Post updatePost(Post post) {
//        return postRepository.save(post);
//    }
//
//    public void deletePost(ObjectId id) {
//        postRepository.deleteById(id);
//    }

}
