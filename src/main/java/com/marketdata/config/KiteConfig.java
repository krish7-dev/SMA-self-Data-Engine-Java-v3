package com.marketdata.config;

import com.zerodhatech.kiteconnect.KiteConnect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KiteConfig {

    @Value("${kite.apiKey}")
    private String apiKey;

    @Value("${kite.userId}")
    private String userId;

    @Value("${kite.accessToken}")
    private String accessToken;

    @Value("${kite.publicToken}")
    private String publicToken;

    @Bean
    public KiteConnect kiteConnect() {
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setUserId(userId);
        kiteConnect.setAccessToken(accessToken);
        kiteConnect.setPublicToken(publicToken); // Optional
        return kiteConnect;
    }
}
