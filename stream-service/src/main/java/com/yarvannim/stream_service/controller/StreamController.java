package com.yarvannim.stream_service.controller;

import com.yarvannim.stream_service.business.implementation.SongService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class StreamController {
    private final SongService songService;

    @GetMapping("api/stream/song/{songId}")
    public Mono<ResponseEntity<String>> getSongStream(@PathVariable String songId) {
        return songService.generateSongStreamUrl(songId)
                .map(url -> ResponseEntity.ok().body(url))
                .onErrorResume(error -> {
                    if(error instanceof IllegalArgumentException){
                        return Mono.just(ResponseEntity.badRequest().body("Invalid song id format."));
                    } else if (error.getMessage() != null && error.getMessage().contains("not found")) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }else {
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating song stream URL."));
                    }
                });
    }
}
