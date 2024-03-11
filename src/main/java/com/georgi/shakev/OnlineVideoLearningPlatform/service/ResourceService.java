package com.georgi.shakev.OnlineVideoLearningPlatform.service;

import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Lesson;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    void uploadProfilePicture(User user, MultipartFile video) throws IOException;
    void uploadVideo(Lesson lesson, MultipartFile video) throws IOException;
    ResponseEntity<?> getResource(Long resourceId);
}
