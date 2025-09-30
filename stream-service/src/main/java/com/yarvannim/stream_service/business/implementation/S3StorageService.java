package com.yarvannim.stream_service.business.implementation;

import com.yarvannim.stream_service.business.interfaces.ObjectStorageService;
import com.yarvannim.stream_service.config.ObjectStorageProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
public class S3StorageService implements ObjectStorageService {
    private final S3AsyncClient s3AsyncClient;
    private final S3Presigner s3Presigner;
    private final ObjectStorageProperties props;

    public S3StorageService(S3AsyncClient s3AsyncClient, S3Presigner s3Presigner, ObjectStorageProperties objectStorageProperties) {
        this.s3AsyncClient = s3AsyncClient;
        this.s3Presigner = s3Presigner;
        this.props = objectStorageProperties;
    }


    @Override
    public Mono<String> generatePresignedUrl(String objectKey) {
        return Mono.fromCallable(() -> {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(props.getBucketName())
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(props.getPresignedUrlExpirationMinutes()))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        });
    }

    @Override
    public Mono<Boolean> objectExists(String objectKey) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(props.getBucketName())
                .key(objectKey)
                .build();
        return Mono.fromFuture(() -> s3AsyncClient.headObject(headObjectRequest))
                .thenReturn(true)
                .onErrorResume(error ->{
                    System.err.println("HEAD_OBJECT FAILED for key: " + objectKey + ". Error: " + error.getClass().getSimpleName() + " - " + error.getMessage());
                    return Mono.just(false);
                });
    }

    @Override
    public String getServiceName() {
        return "S3 Storage";
    }
}
