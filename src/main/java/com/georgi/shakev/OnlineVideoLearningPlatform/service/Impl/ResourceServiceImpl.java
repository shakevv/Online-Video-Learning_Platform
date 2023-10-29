package com.georgi.shakev.OnlineVideoLearningPlatform.service.Impl;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.ResourceResponseDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Lesson;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Resource;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.ResourceNotFoundException;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.LessonRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.ResourceRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.UserRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.ResourceService;
import com.georgi.shakev.OnlineVideoLearningPlatform.store.ResourceStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
public class ResourceServiceImpl implements ResourceService {

    private final ResourceStore resourceStore;
    private final ResourceRepository resourceRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Autowired
    public ResourceServiceImpl(ResourceStore resourceStore,
                               ResourceRepository resourceRepository,
                               LessonRepository lessonRepository,
                               UserRepository userRepository) {
        this.resourceStore = resourceStore;
        this.resourceRepository = resourceRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void uploadProfilePicture(User user, MultipartFile file) throws IOException {
        Resource r = new Resource();
        r.setMimeType(file.getContentType());
        resourceStore.setContent(r, file.getInputStream());
        user.setProfilePicture(resourceRepository.save(r));
        userRepository.save(user);
        new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public void uploadVideo(Lesson lesson, MultipartFile file) throws IOException {
        Resource r = new Resource();
        r.setMimeType(file.getContentType());
        resourceStore.setContent(r, file.getInputStream());
        lesson.setVideo(resourceRepository.save(r));
        lessonRepository.save(lesson);
        new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getResource(Long resourceId) {
        Resource r = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));
            InputStreamResource inputStreamResource = new InputStreamResource(resourceStore.getContent(r));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(r.getContentLength());
            headers.set("Content-Type", r.getMimeType());
            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

    private ResourceResponseDto entityToDto(Resource resource){
        ResourceResponseDto dto = new ResourceResponseDto();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        return dto;
    }
}
