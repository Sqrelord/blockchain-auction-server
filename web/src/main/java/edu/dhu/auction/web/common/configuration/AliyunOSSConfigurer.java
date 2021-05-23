package edu.dhu.auction.web.common.configuration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunOSSConfigurer {
    private static String ACCESS_KEY_ID;
    private static String ACCESS_SECRET;
    public static String BUCKET_NAME;
    public static String ENDPOINT;
    public static String IMAGE_URL;

    @Bean(destroyMethod = "shutdown")
    public OSS getAliyunOss() {
        return new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_SECRET);
    }

    @Value("${aliyuncs.access-key-id}")
    public void setAccessKeyId(String accessKeyId) {
        ACCESS_KEY_ID = accessKeyId;
    }

    @Value("${aliyuncs.access-secret}")
    public void setAccessSecret(String accessSecret) {
        ACCESS_SECRET = accessSecret;
    }

    @Value("${aliyuncs.bucket-name}")
    public void setBucketName(String bucketName) {
        BUCKET_NAME = bucketName;
    }

    @Value("${aliyuncs.endpoint}")
    public void setEndpoint(String endpoint) {
        ENDPOINT = endpoint;
    }

    @Value("${aliyuncs.image-url}")
    public void setImageUrl(String imageUrl) {
        IMAGE_URL = imageUrl;
    }
}
