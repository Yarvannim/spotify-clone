package com.yarvannim.stream_service.repository;

import com.yarvannim.stream_service.domain.entity.Song;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SongRepository extends ReactiveCassandraRepository<Song, UUID> {

    @AllowFiltering
    Mono<Song> findBySongId(UUID songId);

    Mono<Boolean> existsBySongId(UUID songId);
}
