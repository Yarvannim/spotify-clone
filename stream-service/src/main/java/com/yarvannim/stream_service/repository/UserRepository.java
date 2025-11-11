package com.yarvannim.stream_service.repository;

import com.yarvannim.stream_service.domain.entity.User;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCassandraRepository<User, UUID> {

    @Query("SELECT * FROM users WHERE userId = ?0")
    Mono<User> findByUserId(UUID userId);

    @Query("UPDATE users SET isArtist ?1, updatedAt = toTimestamp(now()) WHERE userId = ?0")
    Mono<Boolean> updateArtistStatus(UUID userId, Boolean isArtist);

    @Query("UPDATE users SET displayName = ?1, updatedAt = toTimestamp(now()) WHERE userId = ?0")
    Mono<Boolean> updateDisplayName(UUID userId, String displayName);

    @Query("DELETE FROM users WHERE userId = ?0")
    Mono<Boolean> deleteUser(UUID userId);

    @Query("SELECT * FROM users where lastActiveAt <?0 ALLOW FILTERING")
    Flux<User> findUsersByInactiveSince(Instant cutoffDate);
}
