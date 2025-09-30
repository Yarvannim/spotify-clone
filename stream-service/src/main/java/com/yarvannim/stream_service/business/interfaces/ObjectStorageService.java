package com.yarvannim.stream_service.business.interfaces;

import reactor.core.publisher.Mono;

public interface ObjectStorageService {
    Mono<String> generatePresignedUrl(String objectKey);
    Mono<Boolean> objectExists(String objectKey);
    String getServiceName();
}
