package com.georgi.shakev.OnlineVideoLearningPlatform.service;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.UserRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {
    UserResponseDto createUser(UserRequestDto newUser);

    UserResponseDto updateUser(String username, UserRequestDto userRequest);

    UserResponseDto makeModerator(String username);

    UserResponseDto makeAdmin(String username);

    UserResponseDto removeModerator(String username);

    UserResponseDto removeAdmin(String username);

    UserResponseDto getUser(String username, String accessedByUsername);

    Page<UserResponseDto> getAllUsers(int pageNo, int pageSize, String sortBy, String keyword, String accessedByUsername);

    void deleteUser(String username);

    void uploadProfilePicture(String username, MultipartFile picture);

    ResponseEntity<?> viewProfilePicture(String username);

    void removeProfilePicture(String username);
}
