package com.devcom.OnlyDev.Config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://150.242.201.187/minio/")
                .credentials("minioadmin", "minioadmin")
                .build();
    }
}