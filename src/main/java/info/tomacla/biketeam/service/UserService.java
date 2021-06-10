package info.tomacla.biketeam.service;

import info.tomacla.biketeam.domain.user.User;
import info.tomacla.biketeam.domain.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getByStravaId(Long stravaId) {
        return userRepository.findByStravaId(stravaId);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> get(String userId) {
        return userRepository.findById(userId);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public void promote(String userId) {
        log.info("Request user promotion to admin {}", userId);
        get(userId).ifPresent(user -> {
            user.setAdmin(true);
            save(user);
        });
    }

    public void relegate(String userId) {
        log.info("Request user relegation {}", userId);
        get(userId).ifPresent(user -> {
            user.setAdmin(false);
            save(user);
        });
    }

    public List<User> listUsersWithMailActivated() {
        return userRepository.findByEmailNotNull();
    }
}
