package com.georgi.shakev.OnlineVideoLearningPlatform.repository;

import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
