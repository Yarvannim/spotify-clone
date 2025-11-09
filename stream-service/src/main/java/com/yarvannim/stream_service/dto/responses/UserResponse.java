package com.yarvannim.stream_service.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private UUID userId;
    private String username;
    private String displayName;
    private Boolean isArtist;
    private String createdAt;
}
