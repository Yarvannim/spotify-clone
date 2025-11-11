package com.yarvannim.stream_service.business.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;

public class AuthUtils {

    public static UUID extractUserIdFromAuthentication(Authentication authentication){
        if (!(authentication instanceof JwtAuthenticationToken jwtToken)) {
            return null;
        }

        String userIdStr = jwtToken.getToken().getClaimAsString("sub");
        return UUID.fromString(userIdStr);
    }
}
