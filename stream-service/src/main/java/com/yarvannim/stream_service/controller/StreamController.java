package com.yarvannim.stream_service.controller;

import com.yarvannim.stream_service.business.implementation.ObjectStorageServiceFactory;
import com.yarvannim.stream_service.business.interfaces.ObjectStorageService;
import com.yarvannim.stream_service.dto.responses.getPreSignedUrlResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StreamController {

    private final ObjectStorageServiceFactory objectStorageServiceFactory;

    public StreamController(ObjectStorageServiceFactory objectStorageServiceFactory) {
        this.objectStorageServiceFactory = objectStorageServiceFactory;
    }

    @GetMapping("api/stream/song/{songId}")
    public Mono<ResponseEntity<Map<String, Object>>> getSongStream(@PathVariable String songId) {
        ObjectStorageService storageService = objectStorageServiceFactory.getObjectStorageService();
        String objectKey = songId + ".mp3";

        return storageService.objectExists(objectKey)
                .flatMap(exists -> {
                    Map<String, Object> response = new HashMap<>();

                    if (!exists) {
                        response.put("success", false);
                        response.put("songId", songId);
                        response.put("error", "Song not found in storage");
                        response.put("message", "Failed to generate URL");
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
                    }

                    return storageService.generatePresignedUrl(objectKey)
                            .map(url -> {
                                response.put("success", true);
                                response.put("url", url);
                                response.put("songId", songId);
                                response.put("message", "Pre-signed URL generated successfully");
                                return ResponseEntity.ok(response);
                            });
                })
                .onErrorResume(error -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("songId", songId);
                    response.put("error", error.getMessage());
                    response.put("message", "Internal server error");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
                });
    }
}
