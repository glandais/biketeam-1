package info.tomacla.biketeam.service;

import info.tomacla.biketeam.common.amqp.Queues;
import info.tomacla.biketeam.common.file.FileExtension;
import info.tomacla.biketeam.common.file.FileRepositories;
import info.tomacla.biketeam.common.file.ImageDescriptor;
import info.tomacla.biketeam.domain.team.Team;
import info.tomacla.biketeam.domain.team.Visibility;
import info.tomacla.biketeam.domain.user.User;
import info.tomacla.biketeam.domain.user.UserRepository;
import info.tomacla.biketeam.security.Authorities;
import info.tomacla.biketeam.service.amqp.dto.UserProfileImageDTO;
import info.tomacla.biketeam.service.file.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.strava-id}")
    private Long adminStravaId;

    @Value("${admin.first-name}")
    private String adminFirstName;

    @Value("${admin.last-name}")
    private String adminLastName;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RideService rideService;

    @Autowired
    private TripService tripService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FileService fileService;

    public Optional<User> getByStravaId(Long stravaId) {
        return userRepository.findByStravaId(stravaId);
    }

    public Optional<User> getByFacebookId(String facebookId) {
        return userRepository.findByFacebookId(facebookId);
    }

    public Optional<User> getByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> get(String userId) {
        return userRepository.findById(userId);
    }

    public List<User> listUsers() {
        return userRepository.findAllByOrderByIdAsc();
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

    public boolean authorizeAdminAccess(Authentication authentication, String teamId) {
        return authentication.getAuthorities().contains(Authorities.admin())
                || authentication.getAuthorities().contains(Authorities.teamAdmin(teamId));
    }

    public boolean authorizePublicAccess(Authentication authentication, String teamId) {

        if (authentication.getAuthorities().contains(Authorities.admin())
                || authentication.getAuthorities().contains(Authorities.teamAdmin(teamId))) {
            return true;
        }

        final Team team = teamService.get(teamId).orElseThrow(() -> new IllegalArgumentException("Unknown team " + teamId));
        if (team.getVisibility().equals(Visibility.PUBLIC) || team.getVisibility().equals(Visibility.PUBLIC_UNLISTED)) {
            return true;
        }

        return authentication.getAuthorities().contains(Authorities.teamUser(teamId));

    }

    public List<User> listUsersWithMailActivated(Team team) {
        return userRepository.findByEmailNotNullAndRoles_Team(team);
    }

    public Optional<ImageDescriptor> getImage(String userId) {

        Optional<FileExtension> fileExtensionExists = fileService.fileExists(FileRepositories.USER_IMAGES, userId, FileExtension.byPriority());

        if (fileExtensionExists.isPresent()) {

            final FileExtension extension = fileExtensionExists.get();
            final Path path = fileService.getFile(FileRepositories.USER_IMAGES, userId + extension.getExtension());

            return Optional.of(ImageDescriptor.of(extension, path));

        }

        return Optional.empty();

    }

    @RabbitListener(queues = Queues.TASK_DOWNLOAD_PROFILE_IMAGE)
    public void downloadUserImage(UserProfileImageDTO dto) {
        try {

            Optional<FileExtension> fileExtension = FileExtension.findByFileName(dto.profileImage);
            if (fileExtension.isPresent()) {
                RestTemplate rest = new RestTemplate();
                byte[] imageBytes = rest.getForObject(dto.profileImage, byte[].class);
                Path targetTmpFile = fileService.getTempFile("profile", fileExtension.get().getExtension());
                Files.write(targetTmpFile, imageBytes);
                fileService.storeFile(targetTmpFile, FileRepositories.USER_IMAGES, dto.id + fileExtension.get().getExtension());
            }

        } catch (Exception e) {
            log.error("Unable to download user image " + dto.profileImage);
        }
    }

    @Transactional
    public void delete(User user) {
        try {
            log.info("Request user deletion {}", user.getId());
            // remove all access to this user
            userRoleService.deleteByUser(user.getId());
            rideService.deleteByUser(user.getId());
            tripService.deleteByUser(user.getId());
            messageService.deleteByUser(user.getId());
            // finaly delete the user
            userRepository.deleteById(user.getId());
            log.info("User deleted {}", user.getId());
        } catch (Exception e) {
            log.error("Unable to delete user " + user.getId(), e);
        }
    }

    @PostConstruct
    public void init() {

        log.info("Initializing application data");

        if (getByStravaId(adminStravaId).isEmpty()) {

            User root = new User();
            root.setAdmin(true);
            root.setFirstName(adminFirstName);
            root.setLastName(adminLastName);
            root.setStravaId(adminStravaId);
            save(root);
        }

    }


}