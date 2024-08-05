package com.georgi.shakev.OnlineVideoLearningPlatform.service;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.LessonRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.LessonResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface LessonService {
    LessonResponseDto createLesson(LessonRequestDto lesson, String username);

    LessonResponseDto getLesson(Long lessonId, String accessedByUsername);

    Page<LessonResponseDto> getAllLessons(int pageNo, int pageSize, String sortBy, String searchFor);

    void uploadVideo(Long lessonId, MultipartFile video);

    ResponseEntity<?> viewVideo(Long lessonId);

    void removeVideo(Long lessonId);

    void deleteLesson(Long lessonId);
}
