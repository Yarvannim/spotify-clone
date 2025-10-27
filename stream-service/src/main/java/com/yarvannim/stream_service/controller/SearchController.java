package com.yarvannim.stream_service.controller;

import com.yarvannim.stream_service.business.implementation.SongSearchService;
import com.yarvannim.stream_service.dto.responses.SongSearchResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SongSearchService songSearchService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SongSearchResponse> searchSongs(@RequestParam String q) {
        return songSearchService.fuzzySearch(q);
    }
}
