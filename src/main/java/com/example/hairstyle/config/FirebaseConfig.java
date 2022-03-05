package com.example.hairstyle.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "firebase")
@Getter
@Setter
public class FirebaseConfig {
    private String bucketName;
    private String projectID;
    private String type;
    private String privateKeyID;
    private String privateKey;
    private String clientEmail;
    private String clientID;
    private String authURI;
    private String tokenURI;
    private String authProviderX509CertURL;
    private String clientX509CertURL;
}
