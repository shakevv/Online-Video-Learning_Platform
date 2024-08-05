package com.georgi.shakev.OnlineVideoLearningPlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;

    private String username;

    private Long profilePictureId;

    private Set<String> roles = new HashSet<>();
}
