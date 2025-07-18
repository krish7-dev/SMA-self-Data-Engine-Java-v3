package com.marketdata.config;

import com.marketdata.db.TokenQuery;
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

    private final TokenQuery tokenQuery;

    // Constructor injection (preferred for Spring)
    public KiteConfig(TokenQuery tokenQuery) {
        this.tokenQuery = tokenQuery;
    }

    @Bean
    public KiteConnect kiteConnect() {
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setUserId(userId);

        try {
            String accessToken = tokenQuery.getLatestAccessToken();
            String publicToken = tokenQuery.getLatestPublicToken();

            if (accessToken != null && publicToken != null) {
                kiteConnect.setAccessToken(accessToken);
                kiteConnect.setPublicToken(publicToken);
                System.out.println("✅ Loaded access/public token from database.");
            } else {
                System.out.println("⚠️ No tokens found in DB. Please login via /login/kite");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load tokens from DB: " + e.getMessage());
        }

        return kiteConnect;
    }
}
