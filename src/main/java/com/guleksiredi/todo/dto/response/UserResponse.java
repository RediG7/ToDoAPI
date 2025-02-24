package com.guleksiredi.todo.dto.response;

import com.guleksiredi.todo.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private UserRole role;
}