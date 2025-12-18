package com.devcom.OnlyDev.Service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MediaStorageService {

    private final MinioClient minioClient;

    public MediaStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void upload(
            String bucket,
            String objectKey,
            InputStream inputStream,
            long size,
            String contentType
    ) throws Exception {

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .stream(inputStream, size, -1)
                        .contentType(
                                contentType != null
                                        ? contentType
                                        : "application/octet-stream"
                        )
                        .build()
        );
    }

}
