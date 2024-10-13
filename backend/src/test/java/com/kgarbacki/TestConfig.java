package com.kgarbacki;

import com.kgarbacki.s3.S3Buckets;
import com.kgarbacki.s3.S3Service;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class TestConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public S3Client s3Client() {return new S3Client() {
            @Override
            public String serviceName() {
                return "";
            }

            @Override
            public void close() {

            }
        };
    }


    @Bean
    public S3Service s3Service() {return new S3Service(s3Client());}

    @Bean
    public S3Buckets s3Buckets() {return new S3Buckets();}
}
