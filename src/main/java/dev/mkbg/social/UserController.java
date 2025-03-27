package dev.mkbg.social;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostRepository postRepository;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            User user = userService.registerUser(
                    request.get("username"),
                    request.get("email"),
                    request.get("password")
            );

            Date joinDate = user.getUserId().getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String formattedJoinDate = dateFormat.format(joinDate);

            return ResponseEntity.ok(Map.of(
                    "userId", user.getUserId().toString(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "profilePicture", user.getProfilePicture() == null ? "":user.getProfilePicture(),
                    "joinDate", formattedJoinDate
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        Optional<User> authenticatedUser = userService.authenticateUser(request.get("username"), request.get("password"));
        if (authenticatedUser.isPresent()) {

            User user = authenticatedUser.get();
            Date joinDate = user.getUserId().getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String formattedJoinDate = dateFormat.format(joinDate);

            return ResponseEntity.ok(Map.of(
                    "userId", user.getUserId().toString(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "profilePicture", user.getProfilePicture() == null ? "":user.getProfilePicture(),
                    "joinDate", formattedJoinDate
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable String userId) {
        try {
            User user = userService.stringUserById(userId);
            Date joinDate = user.getUserId().getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String formattedJoinDate = dateFormat.format(joinDate);

            return ResponseEntity.ok(Map.of(
                    "publishedPostsCount", user.getPublishedPostsCount(),
                    "likedPostsCount", user.getLikedPostsCount(),
                    "dislikedPostsCount", user.getDislikedPostsCount(),
                    "publishedCommentsCount", user.getPublishedCommentsCount(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "joinDate", formattedJoinDate
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/published")
    public ResponseEntity<?> getUserPublishedPosts(@PathVariable String userId) {
        try {
            List<ObjectId> postIds = userService.getUserPublishedPosts(userId);
            List<Post> posts = postRepository.findAllById(postIds);
            List<Map<String, Object>> publishedPosts = new ArrayList<>();
            for (Post post : posts) {
                Map<String, Object> map = Map.of(
                        "postId", post.getPostId().toString(),
                        "title", post.getTitle()
                );
                publishedPosts.add(map);
            }
            return ResponseEntity.ok(publishedPosts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/{userId}/liked")
    public ResponseEntity<?> getUserLikedPosts(@PathVariable String userId) {
        try {
            List<ObjectId> postIds = userService.getUserLikedPosts(userId);
            List<Post> posts = postRepository.findAllById(postIds);
            List<Map<String, Object>> likedPosts = new ArrayList<>();
            for (Post post : posts) {
                Map<String, Object> map = Map.of(
                        "postId", post.getPostId().toString(),
                        "title", post.getTitle()
                );
                likedPosts.add(map);
            }
            return ResponseEntity.ok(likedPosts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/disliked")
    public ResponseEntity<?> getUserDislikedPosts(@PathVariable String userId) {
        try {
            List<ObjectId> postIds = userService.getUserDislikedPosts(userId);
            List<Post> posts = postRepository.findAllById(postIds);
            List<Map<String, Object>> disLikedPosts = new ArrayList<>();
            for (Post post : posts) {
                Map<String, Object> map = Map.of(
                        "postId", post.getPostId().toString(),
                        "title", post.getTitle()
                );
                disLikedPosts.add(map);
            }
            return ResponseEntity.ok(disLikedPosts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}