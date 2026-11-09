package com.pviengineers.careersapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AwsClientConfig {

    @Bean
    @ConditionalOnProperty(name = "app.storage.provider", havingValue = "s3")
    public S3Client s3Client(@Value("${app.storage.s3.region:${AWS_REGION:us-west-2}}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.storage.provider", havingValue = "s3")
    public S3Presigner s3Presigner(@Value("${app.storage.s3.region:${AWS_REGION:us-west-2}}") String region) {
        return S3Presigner.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.mail.provider", havingValue = "ses")
    public SesClient sesClient(@Value("${app.mail.ses.region:${AWS_REGION:us-west-2}}") String region) {
        return SesClient.builder()
                .region(Region.of(region))
                .build();
    }
}
