package com.yarvannim.stream_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SongDTO {
    private UUID songId;
    private String title;
    private UUID artistId;
    private String artistName;
    private String storageUrl;
}
