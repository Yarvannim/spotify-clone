package com.yarvannim.stream_service.business.implementation;

import com.yarvannim.stream_service.business.interfaces.ObjectStorageService;
import com.yarvannim.stream_service.config.ObjectStorageProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import lombok.Setter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Service
@Setter
public class MinioStorageService implements ObjectStorageService {
    private final MinioClient minioClient;
    private final ObjectStorageProperties props;

    public MinioStorageService(MinioClient minioClient, ObjectStorageProperties objectStorageProperties) {
        this.minioClient = minioClient;
        this.props = objectStorageProperties;
    }

    @Override
    public Mono<String> generatePresignedUrl(String objectKey) {
        return Mono.fromCallable(() -> {
            try {
                System.out.println("expiration is" + props.getPresignedUrlExpirationMinutes());
                return minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(props.getBucketName())
                                .object(objectKey)
                                .expiry(props.getPresignedUrlExpirationMinutes(), TimeUnit.MINUTES)
                                .build()
                );
            }catch (ErrorResponseException e) {
                System.err.println("MinIO error: " + e.errorResponse().toString());
                throw new RuntimeException(e);
            }catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        );
    }

    @Override
    public Mono<Boolean> objectExists(String objectKey) {
        return Mono.fromCallable(() -> {
           try {
               minioClient.statObject(StatObjectArgs.builder()
                       .bucket(props.getBucketName())
                       .object(objectKey)
                       .build());
               return true;
           }catch (ErrorResponseException e) {
               if (e.errorResponse().code().equals("NoSuchKey")) {
                   return false;
               }
               throw new RuntimeException("MinIO error checking for object: " + e.errorResponse().toString());
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
        });
    }

    @Override
    public String getServiceName() {
        return "Minio";
    }
}
