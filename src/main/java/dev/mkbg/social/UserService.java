package dev.mkbg.social;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUsernameById(ObjectId userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getUsername() : "Unknown User";
    }

    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return userOptional;
        }
        return Optional.empty();
    }

    public User stringUserById(String userId) {
        ObjectId objectId = new ObjectId(userId);
        return userRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<ObjectId> getUserPublishedPosts(String userId) {
        ObjectId objectId = new ObjectId(userId);
        User user = userRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPublishedPosts();
    }

    public List<ObjectId> getUserLikedPosts(String userId) {
        ObjectId objectId = new ObjectId(userId);
        User user = userRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getLikedPosts();
    }

    public List<ObjectId> getUserDislikedPosts(String userId) {
        ObjectId objectId = new ObjectId(userId);
        User user = userRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getDislikedPosts();
    }
}
