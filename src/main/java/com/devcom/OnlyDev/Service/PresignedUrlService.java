package com.devcom.OnlyDev.Service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

@Service
public class PresignedUrlService {

    private final MinioClient minioClient;

    public PresignedUrlService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String generateUploadUrl(String objectKey) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket("post")
                        .object(objectKey)
                        .expiry(10 * 60)
                        .build()
        );
    }
}
