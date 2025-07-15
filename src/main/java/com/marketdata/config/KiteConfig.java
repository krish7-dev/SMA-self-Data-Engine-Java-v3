package com.marketdata.config;

import com.marketdata.engine.KiteStreamer;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class KiteConfig {

    @Value("${kite.apiKey}")
    private String apiKey;

    @Value("${kite.apiSecret}")
    private String apiSecret; // ✅ MISSING — this caused NullPointerException

    @Value("${kite.userId}")
    private String userId;

    private final KiteStreamer kiteStreamer;

    public KiteConfig(KiteStreamer kiteStreamer){
        this.kiteStreamer = kiteStreamer;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    @Bean
    public KiteConnect kiteConnect() throws IOException {
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setUserId(userId);

        Path accessTokenPath = Path.of("kite.access.token");
        Path publicTokenPath = Path.of("kite.public.token");

        if (Files.exists(accessTokenPath) && Files.exists(publicTokenPath)) {
            try{
                String accessToken = Files.readString(accessTokenPath).trim();
                String publicToken = Files.readString(publicTokenPath).trim();

                kiteConnect.setAccessToken(accessToken);
                kiteConnect.setPublicToken(publicToken);

                System.out.println("✅ Loaded access token from file.");
                Profile profile = kiteConnect.getProfile();
                System.out.println("Valid Token for user : " + profile.userName);
                System.out.println("Starting Streaming from Config : ");
                kiteStreamer.startStreaming();
            } catch (Exception | KiteException e){

            }

        } else {
            System.out.println("⚠️ Token files not found. Please login via /login/kite");
        }

        return kiteConnect;
    }
}
