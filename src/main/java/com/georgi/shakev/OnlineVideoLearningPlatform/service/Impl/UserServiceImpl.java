package com.georgi.shakev.OnlineVideoLearningPlatform.service.Impl;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.UserRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.UserResponseDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Lesson;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Resource;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Review;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.ResourceNotFoundException;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.UserAlreadyExistsException;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.LessonRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.ResourceRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.ReviewRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.UserRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.ResourceService;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    private static final String PASSWORD_REQUIREMENTS = "^(?=.*[a-z])(?=.*).{5,256}$";

    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final ReviewRepository reviewRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ResourceService resourceService,
                           ResourceRepository resourceRepository, ReviewRepository reviewRepository,
                           LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.resourceService = resourceService;
        this.resourceRepository = resourceRepository;
        this.reviewRepository = reviewRepository;
        this.lessonRepository = lessonRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach((r) -> authorities.add(new SimpleGrantedAuthority(r)));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(@NotNull UserRequestDto newUser) {
        Optional<User> user = userRepository.getByUsername(newUser.getUsername());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User with this username already exists.");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        UserResponseDto userResponse = entityToDto(userRepository.save(dtoToEntity(newUser)));
        log.info("User with username {} created", userResponse.getUsername());

        return userResponse;
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(String username, @NotNull UserRequestDto userRequest) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        User saved = doUpdateUser(username, userRequest, user);
        log.info("User {} updated to {}.", username, saved.getUsername());

        return entityToDto(saved);
    }

    private User doUpdateUser(String username, UserRequestDto userRequest, User user) {
        if (userRequest.getUsername() != null
                && userRepository.getByUsername(userRequest.getUsername()).isEmpty()
                && username.length() >= 2) {
            user.setUsername(userRequest.getUsername());
        }

        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponseDto makeModerator(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.getRoles().add(ROLE_MODERATOR);
        UserResponseDto userResponse = entityToDto(userRepository.save(user));
        log.info("User {} has moderator rights", username);

        return userResponse;
    }

    @Override
    @Transactional
    public UserResponseDto removeModerator(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.getRoles().remove(ROLE_MODERATOR);
        UserResponseDto userResponse = entityToDto(userRepository.save(user));
        log.info("User {} does not have moderator rights", username);

        return userResponse;
    }

    @Override
    @Transactional
    public UserResponseDto makeAdmin(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.getRoles().add(ROLE_ADMIN);
        UserResponseDto userResponse = entityToDto(userRepository.save(user));
        log.info("User {} has admin rights", username);

        return userResponse;
    }

    @Override
    @Transactional
    public UserResponseDto removeAdmin(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.getRoles().remove(ROLE_ADMIN);
        UserResponseDto userResponse = entityToDto(userRepository.save(user));
        log.info("User {} does not have admin rights", username);

        return userResponse;
    }

    @Override
    public UserResponseDto getUser(String username, String accessedByUsername) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        UserResponseDto userResponse = entityToDto(user);
        log.info("User profile {} opened by {}", userResponse, accessedByUsername);

        return userResponse;
    }

    @Override
    public Page<UserResponseDto> getAllUsers(int pageNo, int pageSize, String sortBy, String keyword, String accessedByUsername) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<User> pagedResult = userRepository.getAllByUsernameContainingIgnoreCase(keyword, paging);

        Page<UserResponseDto> allUsersResponse = pagedResult.map(this::entityToDto);
        log.info("User search for {}, page number {} accessed by {}", keyword, pageNo, accessedByUsername);

        return allUsersResponse;
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        removeUserFromHisReviews(user);
        removeUserFromHisLessons(user);

        userRepository.delete(user);
        log.info("User with username {} deleted", username);
    }

    private void removeUserFromHisReviews(User user) {
        List<Review> userReviews = reviewRepository.findAllByCreator(user);
        userReviews.forEach(review -> review.setCreator(null));
        reviewRepository.saveAll(userReviews);
    }

    private void removeUserFromHisLessons(User user) {
        List<Lesson> userLessons = lessonRepository.findAllByAuthor(user);
        userLessons.forEach(lesson -> lesson.setAuthor(null));
        lessonRepository.saveAll(userLessons);
    }

    @Override
    @Transactional
    public void uploadProfilePicture(String username, MultipartFile picture) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username not found: " + username));

        resourceService.uploadProfilePicture(user, picture);
        userRepository.save(user);
        log.info("User with username {} profile picture uploaded", username);
    }

    @Override
    public ResponseEntity<?> viewProfilePicture(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return resourceService.getResource(user.getProfilePicture().getId());
    }

    @Override
    @Transactional
    public void removeProfilePicture(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Resource picture = user.getProfilePicture();
        user.setProfilePicture(null);

        resourceRepository.delete(picture);
        userRepository.save(user);

        log.info("User with username {} profile picture removed", username);
    }

    private User dtoToEntity(UserRequestDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());

        return user;
    }

    private UserResponseDto entityToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUsername(user.getUsername());
        dto.setId(user.getId());

        if (user.getProfilePicture() != null) {
            dto.setProfilePictureId(user.getProfilePicture().getId());
        }

        user.getRoles().forEach((r) -> dto.getRoles().add(r));
        return dto;
    }
}