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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
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
    public LessonResponseDto createLesson(@NotNull LessonRequestDto lessonDto, @NotNull String username) throws IOException {
        Lesson lesson = dtoToEntity(lessonDto);
        lesson.setAuthor(userRepository.getByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));

        LessonResponseDto newLesson = entityToDto(lessonRepository.save(lesson));
        log.info("Lesson {} created.", newLesson);
        return newLesson;
    }

    @Override
    public LessonResponseDto getLesson(@NotNull Long lessonId, String accessedByUsername) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));
        log.info("Lesson with id {} accessed by {}", lessonId, accessedByUsername);
        return entityToDto(lesson);
    }

    @Override
    @Transactional
    public void deleteLesson(@NotNull Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found for deletion."));

        lessonRepository.delete(lesson);
        log.info("Lesson with id {} deleted", lessonId);
    }

    @Override
    public Page<LessonResponseDto> getAllLessons(int pageNo, int pageSize, String sortBy, String searchFor) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        return lessonRepository.getAllByTitleContainingIgnoreCase(searchFor, paging).map(this::entityToDto);
    }

    @Override
    @Transactional
    public void uploadVideo(@NotNull Long lessonId, MultipartFile video) throws IOException {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found for deletion"));

        resourceService.uploadVideo(lesson, video);
        log.info("Video added to lesson with id {}", lessonId);
    }

    @Override
    public ResponseEntity<?> viewVideo(@NotNull Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found for deletion"));

        log.info("Video of lesson with id {} accessed", lessonId);
        return resourceService.getResource(lesson.getVideo().getId());
    }

    @Override
    @Transactional
    public void removeVideo(@NotNull Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found for deletion"));

        if(lesson.getVideo() == null) {
            throw new ResourceNotFoundException("Video not found.");
        }

        Resource video = lesson.getVideo();
        lesson.setVideo(null);
        resourceRepository.delete(video);
        entityToDto(lessonRepository.save(lesson));
        log.info("Video removed from lesson with id {}", lessonId);
    }

    private LessonResponseDto entityToDto(Lesson lesson){
        LessonResponseDto dto = new LessonResponseDto();
        dto.setId(lesson.getId());
        String authorUsername = lesson.getAuthor() != null ? lesson.getAuthor().getUsername() : null;
        dto.setAuthorUsername(authorUsername);
        dto.setTitle(lesson.getTitle());
        dto.setDescription(lesson.getDescription());
        if(lesson.getVideo() != null) {
            dto.setVideoId(lesson.getVideo().getId());
        }
        return dto;
    }

    private Lesson dtoToEntity(LessonRequestDto dto) {
        Lesson lesson = new Lesson();
        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());
        return lesson;
    }
}
