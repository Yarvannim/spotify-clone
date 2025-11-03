package com.yarvannim.stream_service.business;

import com.yarvannim.stream_service.business.implementation.ObjectStorageServiceFactory;
import com.yarvannim.stream_service.business.implementation.SongService;
import com.yarvannim.stream_service.business.interfaces.ObjectStorageService;
import com.yarvannim.stream_service.domain.entity.Song;
import com.yarvannim.stream_service.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SongServiceTests {
    @Mock
    private SongRepository songRepository;

    @Mock
    private ObjectStorageServiceFactory objectStorageServiceFactory;

    @Mock
    private ObjectStorageService objectStorageService;

    private SongService songService;
    private final UUID validSongId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final String validSongIdString = "123e4567-e89b-12d3-a456-426614174000";
    private final String invalidSongIdString = "invalid-uuid";
    private final String storageUrl = "songs/123e4567-e89b-12d3-a456-426614174000.mp3";
    private final String presignedUrl = "https://storage.example.com/presigned-url";

    @BeforeEach
    void setUp() {
        songService = new SongService(songRepository, objectStorageServiceFactory);
    }

    @Test
    void generateSongStreamUrl_ValidSongIdAndFileExists_ReturnsPresignedUrl() {
        // Arrange
        Song song = new Song();
        song.setSongId(validSongId);
        song.setStorageUrl(storageUrl);

        when(songRepository.findBySongId(validSongId)).thenReturn(Mono.just(song));
        when(objectStorageServiceFactory.getObjectStorageService()).thenReturn(objectStorageService);
        when(objectStorageService.objectExists(storageUrl)).thenReturn(Mono.just(true));
        when(objectStorageService.generatePresignedUrl(storageUrl)).thenReturn(Mono.just(presignedUrl));

        // Act & Assert
        StepVerifier.create(songService.generateSongStreamUrl(validSongIdString))
                .expectNext(presignedUrl)
                .verifyComplete();
    }

    @Test
    void generateSongStreamUrl_InvalidSongId_ThrowsIllegalArgumentException() {
        // Act & Assert
        StepVerifier.create(songService.generateSongStreamUrl(invalidSongIdString))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Invalid song id format"))
                .verify();
    }

    @Test
    void generateSongStreamUrl_SongNotFound_ThrowsRuntimeException() {
        // Arrange
        when(songRepository.findBySongId(validSongId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(songService.generateSongStreamUrl(validSongIdString))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Song not found"))
                .verify();
    }

    @Test
    void generateSongStreamUrl_ObjectNotFoundInStorage_ThrowsRuntimeException() {
        // Arrange
        Song song = new Song();
        song.setSongId(validSongId);
        song.setStorageUrl(storageUrl);

        when(songRepository.findBySongId(validSongId)).thenReturn(Mono.just(song));
        when(objectStorageServiceFactory.getObjectStorageService()).thenReturn(objectStorageService);
        when(objectStorageService.objectExists(storageUrl)).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(songService.generateSongStreamUrl(validSongIdString))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Song file not found"))
                .verify();
    }

    @Test
    void generateSongStreamUrl_StorageServiceError_PropagatesError() {
        // Arrange
        Song song = new Song();
        song.setSongId(validSongId);
        song.setStorageUrl(storageUrl);

        when(songRepository.findBySongId(validSongId)).thenReturn(Mono.just(song));
        when(objectStorageServiceFactory.getObjectStorageService()).thenReturn(objectStorageService);
        when(objectStorageService.objectExists(storageUrl)).thenReturn(Mono.error(new RuntimeException("Storage error")));

        // Act & Assert
        StepVerifier.create(songService.generateSongStreamUrl(validSongIdString))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Error generating song stream URL"))
                .verify();
    }

    @Test
    void songExists_ValidSongIdAndExists_ReturnsSong() {
        // Arrange
        Song expectedSong = new Song();
        expectedSong.setSongId(validSongId);
        when(songRepository.findBySongId(validSongId)).thenReturn(Mono.just(expectedSong));

        // Act & Assert
        StepVerifier.create(songService.songExists(validSongIdString))
                .expectNext(expectedSong)
                .verifyComplete();
    }

    @Test
    void songExists_ValidSongIdButNotFound_ThrowsRuntimeException() {
        // Arrange
        when(songRepository.findBySongId(validSongId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(songService.songExists(validSongIdString))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Song not found"))
                .verify();
    }

    @Test
    void songExists_InvalidSongId_ThrowsIllegalArgumentException() {
        // Act & Assert
        StepVerifier.create(songService.songExists(invalidSongIdString))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Invalid song id format"))
                .verify();
    }

    @Test
    void generatePresignedUrl_ObjectExists_ReturnsPresignedUrl() {
        // Arrange
        when(objectStorageServiceFactory.getObjectStorageService()).thenReturn(objectStorageService);
        when(objectStorageService.objectExists(storageUrl)).thenReturn(Mono.just(true));
        when(objectStorageService.generatePresignedUrl(storageUrl)).thenReturn(Mono.just(presignedUrl));

        // Act & Assert - Using reflection to test private method through public method
        Song song = new Song();
        song.setStorageUrl(storageUrl);
        when(songRepository.findBySongId(validSongId)).thenReturn(Mono.just(song));

        StepVerifier.create(songService.generateSongStreamUrl(validSongIdString))
                .expectNext(presignedUrl)
                .verifyComplete();
    }

    @Test
    void generatePresignedUrl_ObjectDoesNotExist_ThrowsRuntimeException() {
        // Arrange
        when(objectStorageServiceFactory.getObjectStorageService()).thenReturn(objectStorageService);
        when(objectStorageService.objectExists(storageUrl)).thenReturn(Mono.just(false));

        // Act & Assert - Using reflection to test private method through public method
        Song song = new Song();
        song.setStorageUrl(storageUrl);
        when(songRepository.findBySongId(validSongId)).thenReturn(Mono.just(song));

        StepVerifier.create(songService.generateSongStreamUrl(validSongIdString))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Song file not found"))
                .verify();
    }
}
