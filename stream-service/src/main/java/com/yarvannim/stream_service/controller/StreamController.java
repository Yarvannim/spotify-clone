package com.yarvannim.stream_service.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamController {

    @GetMapping("/stream/song")
    public ResponseEntity<Resource> streamSong() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(new ClassPathResource("static/song.mp3"));
    }
}
