package com.shopspace.storage.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {
    private String bucketName;
    private String imageUrl;
    private String configFile;

    public FirebaseProperties() {
    }

    public FirebaseProperties(String bucketName, String imageUrl, String configFile) {
        this.bucketName = bucketName;
        this.imageUrl = imageUrl;
        this.configFile = configFile;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
