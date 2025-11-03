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
@Table("songs_by_id")
public class Song {
    @PrimaryKey
    private UUID songId;
    private String title;
    private UUID artistId;
    private String artistName;
    private String storageUrl;
    private Integer duration;
    private Long size;
    private String fileFormat;
    private Instant createdAt;
    private Instant updatedAt;
}
