package com.yarvannim.stream_service.business;

import com.yarvannim.stream_service.business.implementation.SongSearchService;
import com.yarvannim.stream_service.domain.document.SongDocument;
import com.yarvannim.stream_service.repository.SongSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SongSearchServiceTests {
    @Mock
    private SongSearchRepository songSearchRepository;

    private SongSearchService songSearchService;

    @BeforeEach
    void setUp() {
        songSearchService = new SongSearchService(songSearchRepository);
    }

    @Test
    void fuzzySearch_ValidQuery_ReturnsFluxOfResponses() {
        // Arrange
        SongDocument doc1 = new SongDocument();
        doc1.setId("123e4567-e89b-12d3-a456-426614174000");
        doc1.setTitle("Test Song 1");
        doc1.setArtistName("Artist 1");

        when(songSearchRepository.fuzzySearch("test"))
                .thenReturn(Flux.just(doc1));

        // Act & Assert
        StepVerifier.create(songSearchService.fuzzySearch("test"))
                .expectNextMatches(response ->
                        response.getId().equals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")) &&
                                response.getTitle().equals("Test Song 1") &&
                                response.getArtist().equals("Artist 1")
                )
                .verifyComplete();
    }

    @Test
    void fuzzySearch_RepositoryError_ReturnsEmptyFlux() {
        // Arrange
        when(songSearchRepository.fuzzySearch("test"))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(songSearchService.fuzzySearch("test"))
                .verifyComplete(); // Expect no elements, just completion
    }
}
