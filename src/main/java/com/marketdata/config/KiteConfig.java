package com.marketdata.config;

import com.marketdata.engine.KiteStreamer;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class KiteConfig {

    @Value("${kite.apiKey}")
    private String apiKey;

    @Value("${kite.apiSecret}")
    private String apiSecret;

    @Value("${kite.userId}")
    private String userId;

    private final KiteStreamer kiteStreamer;

    public KiteConfig(KiteStreamer kiteStreamer) {
        this.kiteStreamer = kiteStreamer;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    @Bean
    public KiteConnect kiteConnect() {
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setUserId(userId);
        return kiteConnect;
    }

    public boolean configureAndStart(KiteConnect kiteConnect) {
        Path accessTokenPath = Path.of("kite.access.token");
        Path publicTokenPath = Path.of("kite.public.token");

        if (Files.exists(accessTokenPath) && Files.exists(publicTokenPath)) {
            try {
                String accessToken = Files.readString(accessTokenPath).trim();
                String publicToken = Files.readString(publicTokenPath).trim();

                kiteConnect.setAccessToken(accessToken);
                kiteConnect.setPublicToken(publicToken);

                Profile profile = kiteConnect.getProfile();
                System.out.println("✅ Valid Token for user : " + profile.userName);
                kiteStreamer.startStreaming();
                return true;
            } catch (Exception | KiteException e) {
                System.out.println("⚠️ Token setup failed: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Token files not found. Please login via /login/kite");
        }
        return false;
    }

    @PostConstruct
    public void tryAutoStart() {
        System.out.println("🚀 Attempting auto-start of streaming with saved tokens...");
        boolean success = configureAndStart(kiteConnect());
        if (success) {
            System.out.println("✅ Streaming started on app startup.");
        } else {
            System.out.println("⚠️ Streaming NOT started. Awaiting /login/kite.");
        }
    }
}
