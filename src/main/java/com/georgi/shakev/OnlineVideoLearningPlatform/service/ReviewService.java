package com.georgi.shakev.OnlineVideoLearningPlatform.service;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.ReviewRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.ReviewResponseDto;
import org.springframework.data.domain.Page;

public interface ReviewService {
    void createReview(ReviewRequestDto reviewRequest, String createdByUsername);
    void deleteReview(Long reviewId, String deletedByUsername);
    Page<ReviewResponseDto> getReviews(int page, Long lessonId);
}
