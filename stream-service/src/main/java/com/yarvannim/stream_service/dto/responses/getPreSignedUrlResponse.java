package com.yarvannim.stream_service.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class getPreSignedUrlResponse {
    private boolean success;
    private String url;
    private String songId;
    private String message;

    public static getPreSignedUrlResponse success(String url, String songId) {
        return new getPreSignedUrlResponse(true, url, songId, "Pre-signed url created successfully");
    }

    public static getPreSignedUrlResponse failure(String songId, String message) {
        return new getPreSignedUrlResponse(false, null, songId, message);
    }
}
