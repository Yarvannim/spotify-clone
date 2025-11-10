package com.yarvannim.stream_service.business;

import com.yarvannim.stream_service.business.implementation.UserService;
import com.yarvannim.stream_service.domain.entity.User;
import com.yarvannim.stream_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final UUID TEST_USER_ID = UUID.randomUUID();
    private final String TEST_USER_DISPLAY_NAME = "testuser";
    private final String TEST_USER_NAME = "testuser";
    private final Instant NOW = Instant.now();

    private User getMockUser(Boolean isArtist) {
        return new User(TEST_USER_ID, TEST_USER_NAME, TEST_USER_DISPLAY_NAME, isArtist, NOW, NOW);
    }

    private JwtAuthenticationToken createMockAuthToken(String sub, boolean isArtist, String displayName) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", sub)
                .claim("preferred_username", TEST_USER_NAME)
                .claim("name", displayName)
                .build();

        List<SimpleGrantedAuthority> authorities = isArtist
                ? List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ARTIST"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new JwtAuthenticationToken(jwt, authorities);
    }

    @BeforeEach
    public void setup() {

    }

    @Test
    void syncUserFromKeycloak_shouldCreateNewUserIfNotFound() {
        JwtAuthenticationToken auth = createMockAuthToken(TEST_USER_ID.toString(), false, TEST_USER_DISPLAY_NAME);
        User newUser = getMockUser(false);

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(newUser));

        StepVerifier.create(userService.syncUserFromKeycloak(auth))
                .expectNextMatches(user -> user.getUserId().equals(TEST_USER_ID) && !user.getIsArtist())
                .verifyComplete();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void syncUserFromKeycloak_shouldUpdateExistingUserIfDisplayNameChanged() {
        String OLD_DISPLAY_NAME = "Old Name";
        String NEW_DISPLAY_NAME = "New Name";

        User existingUser = getMockUser(false);
        existingUser.setDisplayName(OLD_DISPLAY_NAME);

        JwtAuthenticationToken auth = createMockAuthToken(TEST_USER_ID.toString(), false, NEW_DISPLAY_NAME);

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.just(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            return Mono.just(userToSave);
        });

        StepVerifier.create(userService.syncUserFromKeycloak(auth))
                .expectNextMatches(user -> user.getDisplayName().equals(NEW_DISPLAY_NAME))
                .verifyComplete();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void  syncUserFromKeycloak_shouldReturnExistingUserIfNoChanges(){
        JwtAuthenticationToken auth = createMockAuthToken(TEST_USER_ID.toString(), false, TEST_USER_DISPLAY_NAME);
        User existingUser = getMockUser(false);

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userService.syncUserFromKeycloak(auth))
                .expectNextMatches(user -> user.getDisplayName().equals(TEST_USER_DISPLAY_NAME))
                .verifyComplete();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void syncUserFromKeycloak_shouldErrorOnInvalidAuthentication() {
        Authentication invalidAuth = mock(Authentication.class);

        StepVerifier.create(userService.syncUserFromKeycloak(invalidAuth))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Invalid authentication type"))
                .verify();

        verify(userRepository, never()).findByUserId(any());
    }

    @Test
    void getUserByUserId_shouldReturnUserResponse_whenFound() {
        User user = getMockUser(false);
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.getUserByUserId(TEST_USER_ID))
                .expectNextMatches(response -> response.getUserId().equals(TEST_USER_ID))
                .verifyComplete();
    }

    @Test
    void getUserByUserId_shouldError_whenNotFound(){
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserByUserId(TEST_USER_ID))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().contains("User not found"))
                .verify();
    }

    @Test
    void getCurrentUser_shouldReturnUserResponse(){
        JwtAuthenticationToken auth = createMockAuthToken(TEST_USER_ID.toString(), true, TEST_USER_DISPLAY_NAME);
        User mockUser = getMockUser(false);
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.just(mockUser));

        StepVerifier.create(userService.getCurrentUser(auth))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void updateDisplayName_shouldUpdateAndReturnUserResponse(){
        JwtAuthenticationToken auth = createMockAuthToken(TEST_USER_ID.toString(), false, TEST_USER_DISPLAY_NAME);
        User existingUser = getMockUser(false);
        String NEW_NAME = "Renamed User";

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.just(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return Mono.just(user);
        });

        StepVerifier.create(userService.updateDisplayName(auth, NEW_NAME))
                .expectNextMatches(response -> response.getDisplayName().equals(NEW_NAME))
                .verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateDisplayName_shouldError_whenUserNotFound(){
        JwtAuthenticationToken auth = createMockAuthToken(TEST_USER_ID.toString(), false, TEST_USER_DISPLAY_NAME);
        String NEW_NAME = "Renamed User";

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.empty());

        StepVerifier.create(userService.updateDisplayName(auth, NEW_NAME))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().contains("User not found"))
                .verify();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteCurrentUser_shouldSucceed() {
        JwtAuthenticationToken auth = createMockAuthToken(TEST_USER_ID.toString(), false, TEST_USER_DISPLAY_NAME);

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.just(getMockUser(false)));
        when(userRepository.deleteUser(TEST_USER_ID)).thenReturn(Mono.just(true));

        StepVerifier.create(userService.deleteCurrentUser(auth))
                .verifyComplete();

        verify(userRepository, times(1)).deleteUser(TEST_USER_ID);
    }

    @Test
    void deleteCurrentUser_shouldError_whenUserNotFound(){
        JwtAuthenticationToken auth = createMockAuthToken(TEST_USER_ID.toString(), false, TEST_USER_DISPLAY_NAME);

        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteCurrentUser(auth))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().contains("User not found for deletion"))
                .verify();
        verify(userRepository, never()).deleteUser(any());
    }

    @Test
    void isArtist_shouldReturnTrue_whenUserIsArtist() {
        User mockUser = getMockUser(true);
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.just(mockUser));

        StepVerifier.create(userService.isArtist(TEST_USER_ID))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isArtist_shouldReturnFalse_whenUserIsNotArtist() {
        User mockUser = getMockUser(false);
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.just(mockUser));

        StepVerifier.create(userService.isArtist(TEST_USER_ID))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isArtist_shouldReturnFalse_whenUserNotFound(){
        when(userRepository.findByUserId(TEST_USER_ID)).thenReturn(Mono.empty());

        StepVerifier.create(userService.isArtist(TEST_USER_ID))
                .expectNext(false)
                .verifyComplete();
    }
}
