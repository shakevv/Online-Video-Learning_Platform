package com.georgi.shakev.OnlineVideoLearningPlatform.service.Impl;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.LessonRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.LessonResponseDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Lesson;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Resource;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.ResourceNotFoundException;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.LessonRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.ResourceRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.UserRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.LessonService;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;

    @Autowired
    public LessonServiceImpl(LessonRepository lessonRepository, UserRepository userRepository, ResourceService resourceService, ResourceRepository resourceRepository) {
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.resourceService = resourceService;
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional
    public LessonResponseDto createLesson(LessonRequestDto lessonDto, String username) throws IOException {
        Lesson lesson = dtoToEntity(lessonDto);
        lesson.setAuthor(userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
        return entityToDto(lessonRepository.save(lesson));
    }

    @Override
    public LessonResponseDto getLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));
        return entityToDto(lesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found for deletion."));
        lessonRepository.delete(lesson);
    }

    @Override
    public Page<LessonResponseDto> getAllLessons(Integer pageNo, Integer pageSize, String sortBy, String searchFor) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        return lessonRepository.getAllByTitleContainingIgnoreCase(searchFor, paging).map(this::entityToDto);
    }

    @Override
    @Transactional
    public void uploadVideo(Long lessonId, MultipartFile video) throws IOException {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found for deletion"));
        resourceService.uploadVideo(lesson, video);
    }

    @Override
    public ResponseEntity<?> viewVideo(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found for deletion"));
        return resourceService.getResource(lesson.getVideo().getId());
    }

    @Override
    @Transactional
    public void removeVideo(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found for deletion"));
        if(lesson.getVideo() == null)
            throw new ResourceNotFoundException("Video not found.");
        Resource video = lesson.getVideo();
        lesson.setVideo(null);
        resourceRepository.delete(video);
        entityToDto(lessonRepository.save(lesson));
    }

    private LessonResponseDto entityToDto(Lesson lesson){
        LessonResponseDto dto = new LessonResponseDto();
        dto.setId(lesson.getId());
        dto.setAuthorUsername(lesson.getAuthor().getUsername());
        dto.setTitle(lesson.getTitle());
        dto.setDescription(lesson.getDescription());
        if(lesson.getVideo() != null) {
            dto.setVideoId(lesson.getVideo().getId());
        }
        return dto;
    }

    private Lesson dtoToEntity(LessonRequestDto dto) throws IOException {
        Lesson lesson = new Lesson();
        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());
        return lesson;
    }
}
