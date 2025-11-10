package com.yarvannim.stream_service.business.implementation;

import com.yarvannim.stream_service.domain.entity.User;
import com.yarvannim.stream_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@AllArgsConstructor
@Service
public class GdprComplianceService {
    private final UserRepository userRepository;
    private static final Integer INACTIVITY_PERIOD_YEARS = 2;

    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteInactiveUsers(){
        Instant cutOffDate = Instant.now().minus(INACTIVITY_PERIOD_YEARS, ChronoUnit.YEARS);

        userRepository.findUsersByInactiveSince(cutOffDate)
                .flatMap(this::deleteUserAndAssociatedData)
                .collectList()
                .subscribe(deletedUsers -> {
                    if (!deletedUsers.isEmpty()) {
                        log.info("GDPR inactive users cleanup completed. Deleted {} inactive users.", deletedUsers.size());
                    } else {
                        log.info("GDPR inactive users cleanup completed. No users to delete.");
                    }
                }, error -> log.error("GDPR inactive users cleanup failed.", error));
    }

    private Mono<User> deleteUserAndAssociatedData(User user){
        return userRepository.deleteUser(user.getUserId())
                .doOnSuccess(success -> log.debug("Successfully deleted user :{}", user.getUserId()))
                .doOnError(error -> log.error("Failed to delete user {}: {}", user.getUserId(), error.getMessage()))
                .thenReturn(user);
    }
}
