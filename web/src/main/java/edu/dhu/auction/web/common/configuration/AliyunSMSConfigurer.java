package edu.dhu.auction.web.common.configuration;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunSMSConfigurer {
    private static String ACCESS_KEY_ID;
    private static String ACCESS_SECRET;
    private static String REGION_ID;

    @Bean(destroyMethod = "shutdown")
    public IAcsClient getAliyunSMS() {
        DefaultProfile profile = DefaultProfile.getProfile(REGION_ID, ACCESS_KEY_ID, ACCESS_SECRET);
        return new DefaultAcsClient(profile);
    }

    @Value("${aliyuncs.access-key-id}")
    public void setAccessKeyId(String accessKeyId) {
        ACCESS_KEY_ID = accessKeyId;
    }

    @Value("${aliyuncs.access-secret}")
    public void setAccessSecret(String accessSecret) {
        ACCESS_SECRET = accessSecret;
    }

    @Value("${aliyuncs.region-id}")
    public void setREGION_ID(String regionId) {
        REGION_ID = regionId;
    }
}
