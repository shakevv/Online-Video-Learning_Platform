package com.georgi.shakev.OnlineVideoLearningPlatform.repository;

import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Lesson;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Page<Lesson> getAllByTitleContainingIgnoreCase(String title, Pageable pageable);
    List<Lesson> findAllByAuthor(User user);
}
