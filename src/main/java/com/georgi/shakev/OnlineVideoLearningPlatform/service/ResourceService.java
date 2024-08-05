package com.georgi.shakev.OnlineVideoLearningPlatform.service;

import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Lesson;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    void uploadProfilePicture(User user, MultipartFile video);

    void uploadVideo(Lesson lesson, MultipartFile video);

    ResponseEntity<?> getResource(Long resourceId);
}
