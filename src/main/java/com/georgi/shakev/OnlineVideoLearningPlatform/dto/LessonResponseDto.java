package com.georgi.shakev.OnlineVideoLearningPlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponseDto {
    private Long id;
    private String title;
    private String description;
    private String authorUsername;
    private Long videoId;
}
