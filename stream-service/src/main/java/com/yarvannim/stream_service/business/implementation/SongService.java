package com.yarvannim.stream_service.business.implementation;

import com.yarvannim.stream_service.business.interfaces.ObjectStorageService;
import com.yarvannim.stream_service.domain.entity.Song;
import com.yarvannim.stream_service.repository.SongRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Service
public class SongService {
    private final SongRepository songRepository;
    private final ObjectStorageServiceFactory objectStorageServiceFactory;

    public Mono<String> generateSongStreamUrl(String songId){
        return songExists(songId)
                .flatMap(song -> {
                    String objectKey = song.getStorageUrl();
                    return generatePresignedUrl(objectKey);
                })
                .onErrorMap(error -> new RuntimeException("Error generating song stream URL: " + error.getMessage()));
    }

    public Mono<Song> songExists(String songId){
        try {
            UUID uuid = UUID.fromString(songId);
            return songRepository.findBySongId(uuid).switchIfEmpty(Mono.error(new RuntimeException("Song not found.")));
        } catch (Exception e) {
            return Mono.error(new IllegalArgumentException("Invalid song id format: " + songId + "."));
        }
    }

    private Mono<String> generatePresignedUrl(String objectKey){
        ObjectStorageService storageService = objectStorageServiceFactory.getObjectStorageService();
        return storageService.objectExists(objectKey)
                .flatMap(exists -> {
                    if(!exists){
                        return Mono.error(new RuntimeException("Song file not found. Song id: " + objectKey));
                    }
                    return storageService.generatePresignedUrl(objectKey);
                });
    }
}
