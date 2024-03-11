package com.georgi.shakev.OnlineVideoLearningPlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @Size(min = 2, message = "Username length should be at least 2.")
    private String username;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*).{5,256}$",
            message = "Password should be at least 8 characters long.")
    private String password;
}
