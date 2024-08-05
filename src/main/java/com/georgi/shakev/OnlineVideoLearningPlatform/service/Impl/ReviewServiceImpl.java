package com.georgi.shakev.OnlineVideoLearningPlatform.service.Impl;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.ReviewRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.dto.ReviewResponseDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Lesson;
import com.georgi.shakev.OnlineVideoLearningPlatform.entity.Review;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.ResourceNotFoundException;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.LessonRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.ReviewRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.repository.UserRepository;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private static final String sortBy = "id";
    private static final int pageSize = 10;

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             LessonRepository lessonRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    @Transactional
    public void createReview(@NotNull ReviewRequestDto reviewRequest, String createdByUsername) {
        reviewRepository.save(dtoToEntity(reviewRequest));
        log.info("Review {} added by user {}", reviewRequest.getComment(), createdByUsername);
    }

    @Override
    @Transactional
    public void deleteReview(@NotNull Long reviewId, String deletedByUsername) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found."));

        reviewRepository.delete(review);
        log.info("Review with id {} deleted by user {}", reviewId, deletedByUsername);
    }

    @Override
    public Page<ReviewResponseDto> getReviews(int page, @NotNull Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));

        return reviewRepository.getAllByLessonId(
                        lessonId, PageRequest.of(page, pageSize, Sort.by(sortBy).descending()))
                .map(this::entityToDto);
    }

    private ReviewResponseDto entityToDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        String creatorName = review.getCreator() != null ?
                review.getCreator().getUsername() : "Deleted User";
        dto.setReviewCreatorUsername(creatorName);
        dto.setComment(review.getComment());
        dto.setDate(review.getCreated());
        dto.setId(review.getId());
        if (review.getCreator() != null && review.getCreator().getProfilePicture() != null) {
            dto.setCreatorProfilePictureId(review.getCreator().getProfilePicture().getId());
        }
        return dto;
    }

    private Review dtoToEntity(ReviewRequestDto dto) {
        Review review = new Review();
        review.setComment(dto.getComment());
        review.setCreator(userRepository.getByUsername(dto.getReviewCreatorUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
        review.setLesson(lessonRepository.getById(dto.getLessonId()));
        if (review.getLesson() == null) {
            throw new ResourceNotFoundException("Lesson not found.");
        }
        return review;
    }
}
