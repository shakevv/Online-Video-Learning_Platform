package com.georgi.shakev.OnlineVideoLearningPlatform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class LessonRequestDto {
    @NonNull
    @NotNull
    @Size(min = 2, max = 100)
    private String title;

    @NonNull
    @Size(max = 2048)
    private String description;
}
