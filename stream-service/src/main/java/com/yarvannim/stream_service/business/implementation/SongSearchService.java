package com.yarvannim.stream_service.business.implementation;


import com.yarvannim.stream_service.domain.document.SongDocument;
import com.yarvannim.stream_service.dto.responses.SongSearchResponse;
import com.yarvannim.stream_service.repository.SongSearchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SongSearchService {
    private final SongSearchRepository songSearchRepository;

    public List<SongSearchResponse> fuzzySearch(String query){
        try {
            List<SongDocument> songDocuments = songSearchRepository.fuzzySearch(query);
            return songDocuments.stream().map(this::convertToSafeResponse).toList();
        } catch (Exception e) {
            System.out.println("Exception occurred while searching for song: " + e.getMessage());
            return List.of();
        }
    }

    private SongSearchResponse convertToSafeResponse(SongDocument songDocument){
        return new SongSearchResponse(UUID.fromString(songDocument.getId()), songDocument.getTitle(), songDocument.getArtistName());
    }
}
