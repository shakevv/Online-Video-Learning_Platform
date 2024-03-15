package com.georgi.shakev.OnlineVideoLearningPlatform.controller;

import com.georgi.shakev.OnlineVideoLearningPlatform.dto.ReviewRequestDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/home/{lessonId}")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @ModelAttribute("reviewRequest")
    public ReviewRequestDto reviewRequestDto() {
        return new ReviewRequestDto();
    }

    @PostMapping("/add-review")
    public String createReview(ReviewRequestDto reviewRequestDto, @PathVariable Long lessonId, @AuthenticationPrincipal User principal) {
        reviewRequestDto.setReviewCreatorUsername(principal.getUsername());
        reviewRequestDto.setLessonId(lessonId);
        reviewService.createReview(reviewRequestDto, principal.getUsername());
        return "redirect:/home/" + reviewRequestDto.getLessonId();
    }

    @PreAuthorize("principal.username == #username or hasRole('ADMIN')")
    @GetMapping("/delete-review")
    public String deleteReview(@RequestParam(value = "reviewId", defaultValue = "0") Long reviewId,
                               @PathVariable Long lessonId,
                               @AuthenticationPrincipal User principal) {
        reviewService.deleteReview(reviewId, principal.getUsername());
        return "redirect:/home/" + lessonId;
    }
}
