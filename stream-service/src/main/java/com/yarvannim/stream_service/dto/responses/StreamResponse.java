package com.yarvannim.stream_service.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StreamResponse {
    private String songId;
    private String url;
}
