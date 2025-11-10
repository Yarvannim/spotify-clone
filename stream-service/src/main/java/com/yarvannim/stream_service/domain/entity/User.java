package com.yarvannim.stream_service.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table("users")
public class User {
    @PrimaryKey
    private UUID userId;
    private String username;
    private String displayName;
    private Boolean isArtist;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastActiveAt;
}
