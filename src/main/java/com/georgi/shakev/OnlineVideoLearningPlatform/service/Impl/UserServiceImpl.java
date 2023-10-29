package com.georgi.shakev.OnlineVideoLearningPlatform.service.Impl;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.UserRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.UserResponseDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Resource;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Review;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.ResourceNotFoundException;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.UserAlreadyExistsException;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.ResourceRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.ReviewRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.UserRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.ResourceService;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.UserService;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;

    private final ReviewRepository reviewRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ResourceService resourceService,
                           ResourceRepository resourceRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.resourceService = resourceService;
        this.resourceRepository = resourceRepository;
        this.reviewRepository = reviewRepository;
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
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), authorities);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto newUser) {
        Optional<User> user = userRepository.getByUsername(newUser.getUsername());
        if(user.isPresent()){
            throw new UserAlreadyExistsException("User with this username already exists.");
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return entityToDto(userRepository.save(dtoToEntity(newUser)));
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(String username, UserRequestDto userRequest) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
       if(userRequest.getUsername() != null && userRepository.getByUsername(userRequest.getUsername()).isEmpty()
               && username.length() >= 2){
           user.setUsername(userRequest.getUsername());
       }
        if(userRequest.getPassword() != null && userRequest.getPassword()
                .matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,256}$")){
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        User saved = userRepository.save(user);
        return entityToDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto makeModerator(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.getRoles().add("ROLE_MODERATOR");
        return entityToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto makeAdmin(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.getRoles().add("ROLE_ADMIN");
        return entityToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto removeModerator(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.getRoles().remove("ROLE_MODERATOR");
        return entityToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto removeAdmin(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.getRoles().remove("ROLE_ADMIN");
        return entityToDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getUser(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return entityToDto(user);
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Integer pageNo, Integer pageSize, String sortBy, String keyword) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<User> pagedResult = userRepository.getAllByUsernameContainingIgnoreCase(keyword, paging);
        return pagedResult.map(this::entityToDto);
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        removeUserFromHisReviews(user);
        userRepository.delete(user);
    }

    private void removeUserFromHisReviews(User user) {
        List<Review> userReviews = reviewRepository.findAllByCreator(user);
        userReviews.forEach(review -> review.setCreator(null));
        reviewRepository.saveAll(userReviews);
    }

    @Override
    @Transactional
    public void uploadProfilePicture(String username, MultipartFile picture) throws IOException {
       User user = userRepository.getByUsername(username)
               .orElseThrow(() -> new ResourceNotFoundException("User with username not found: " + username));
       resourceService.uploadProfilePicture(user, picture);
        entityToDto(userRepository.save(user));
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
        entityToDto(userRepository.save(user));
    }

    private User dtoToEntity(UserRequestDto dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }

    private UserResponseDto entityToDto(User user){
        UserResponseDto dto = new UserResponseDto();
        dto.setUsername(user.getUsername());
        dto.setId(user.getId());
        if(user.getProfilePicture() != null) {
            dto.setProfilePictureId(user.getProfilePicture().getId());
        }
        user.getRoles().forEach((r) -> dto.getRoles().add(r));
        return dto;
    }
}
