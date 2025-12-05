package dev.mkbg.social;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;

    @GetMapping("/feed")
    public ResponseEntity<?> getAllPosts(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Post> posts = postService.getPostsCursor(cursor, size);
            List<Map<String, Object>> postsInfo = new ArrayList<>();

            for (Post post : posts) {
                Date creationDate = post.getPostId().getDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String formattedCreationDate = dateFormat.format(creationDate);
                User author = postService.getPostAuthor(post);

                Map<String, Object> postInfo = Map.of(
                        "id", post.getPostId().toString(),
                        "title", post.getTitle(),
                        "content", post.getContent(),
                        "picture", post.getPicture() == null ? "" : post.getPicture(),
                        "likesCount", post.getLikesCount(),
                        "dislikesCount", post.getDislikesCount(),
                        "commentsCount", post.getCommentsCount(),
                        "creationDate", formattedCreationDate,
                        "timeStamp", post.getPostId().getTimestamp(),
                        "author", Map.of(
                                "id", author.getUserId().toString(),
                                "username", author.getUsername()
                        )
                );
                postsInfo.add(postInfo);
            }

            String nextCursor = posts.isEmpty() ? null : posts.get(posts.size() - 1).getPostId().toString();
            boolean hasMore = posts.size() == size; // If we got full page, there might be more

            Map<String, Object> response = Map.of(
                    "posts", postsInfo,
                    "nextCursor", nextCursor != null ? nextCursor : "",
                    "hasMore", hasMore
            );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getPostById(@PathVariable String id) {
        Optional<Post> postOptional = postService.getPostById(new ObjectId(id));
        Date creationDate = new ObjectId(id).getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedCreationDate = dateFormat.format(creationDate);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            User author = postService.getPostAuthor(post);

            Map<String, Object> postInfo = Map.of(
                    "id", post.getPostId().toString(),
                    "title", post.getTitle(),
                    "content", post.getContent(),
                    "picture", post.getPicture() == null ?  "":post.getPicture(),
                    "likesCount", post.getLikesCount(),
                    "dislikesCount", post.getDislikesCount(),
                    "commentsCount", post.getCommentsCount(),
                    "creationDate", formattedCreationDate,
                    "timeStamp", post.getPostId().getTimestamp(),
                    "author", Map.of(
                            "id", author.getUserId().toString(),
                            "username", author.getUsername()
                    )
            );
            return ResponseEntity.ok(postInfo);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam String query,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Post> results = postService.searchPostsCursor(query, cursor, size);
            List<Map<String, Object>> postsInfo = new ArrayList<>();

            for (Post post : results) {
                Date creationDate = post.getPostId().getDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String formattedCreationDate = dateFormat.format(creationDate);
                User author = postService.getPostAuthor(post);

                Map<String, Object> postInfo = Map.of(
                        "id", post.getPostId().toString(),
                        "title", post.getTitle(),
                        "content", post.getContent(),
                        "picture", post.getPicture() == null ? "" : post.getPicture(),
                        "likesCount", post.getLikesCount(),
                        "dislikesCount", post.getDislikesCount(),
                        "commentsCount", post.getCommentsCount(),
                        "creationDate", formattedCreationDate,
                        "timeStamp", post.getPostId().getTimestamp(),
                        "author", Map.of(
                                "id", author.getUserId().toString(),
                                "username", author.getUsername()
                        )
                );
                postsInfo.add(postInfo);
            }

            String nextCursor = results.isEmpty() ? "" : results.get(results.size() - 1).getPostId().toString();
            boolean hasMore = results.size() == size;

            Map<String, Object> response = Map.of(
                    "posts", postsInfo,
                    "nextCursor", nextCursor,
                    "hasMore", hasMore
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam Map<String, String> request) {
        String userId = request.get("userId");
        String title = request.get("title");
        String content = request.get("content");

        try {
            String fileId = null;
            if (file != null && !file.isEmpty()) {
                fileId = imageService.storeImage(file);
            }
            postService.createPost(userId, title, content, fileId);
            return ResponseEntity.ok(Map.of("message", "Post created successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable String postId, @RequestParam String userId) {
        Post updatedPost = postService.likePost(postId, userId);
        if (updatedPost != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("postId", updatedPost.getPostId().toString());
            response.put("likesCount", updatedPost.getLikesCount());
            response.put("dislikesCount", updatedPost.getDislikesCount());

            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<?> dislikePost(@PathVariable String postId, @RequestParam String userId) {
        Post updatedPost = postService.dislikePost(postId, userId);
        if (updatedPost != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("postId", updatedPost.getPostId().toString());
            response.put("likesCount", updatedPost.getLikesCount());
            response.put("dislikesCount", updatedPost.getDislikesCount());

            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getPostComments(@PathVariable String postId) {
        try {
            List<Comment> comments = postService.getCommentsByPostId(postId);

            if (comments.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, Object>> commentsList = new ArrayList<>();
            for (Comment comment : comments) {
                Map<String, Object> map = new HashMap<>();
                map.put("commentId", comment.getCommentId().toString());
                map.put("content", comment.getContent());
                map.put("authorName", userService.getUsernameById(comment.getAuthorId()));
                commentsList.add(map);
            }
            return ResponseEntity.ok(commentsList);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{postId}/commentsAdd")
    public ResponseEntity<?> addCommentToPost(@PathVariable String postId, @RequestBody Map<String, String> commentRequest) {
        try {
            String userId = commentRequest.get("userId");
            String content = commentRequest.get("content");
            if (userId == null || userId.isEmpty() || content == null || content.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID/no content"));
            }

            postService.addCommentToPost(postId, userId, content);
            return getPostComments(postId);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
//    @PutMapping
//    public ResponseEntity<Post> updatePost(@RequestBody Post post) {}
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePost(@PathVariable String id) {}
}
