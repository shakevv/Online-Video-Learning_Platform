package com.georgi.shakev.OnlineVideoLearningPlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    @Size(max = 2048)
    private String comment;

    private String reviewCreatorUsername;

    private Long lessonId;
}
