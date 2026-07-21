package org.gmr.app.minidrive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client(
        @Value("${aws.region}") String region,
        @Value("${aws.endpoint}") String endpoint,
        @Value("${aws.access-key}") String accessKey,
        @Value("${aws.secret-key}") String secretKey) {

            AwsBasicCredentials credentials =
                    AwsBasicCredentials.create(accessKey, secretKey);

            return S3Client.builder()
                    .region(Region.of(region))
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(credentials)
                    )
                    .serviceConfiguration(
                            S3Configuration.builder()
                                    .pathStyleAccessEnabled(true)
                                    .build()
                    )
                    .build();
        }

}
