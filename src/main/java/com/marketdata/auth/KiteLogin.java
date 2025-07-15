package com.marketdata.auth;

import com.marketdata.config.KiteConfig;
import com.marketdata.engine.KiteStreamer;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/login")
public class KiteLogin {

    @Value("${kite.apiSecret}")
    private String apiSecret;

    private final KiteConnect kiteConnect;
    private final KiteStreamer kiteStreamer;
    private final KiteConfig kiteConfig;

    public KiteLogin(KiteConnect kiteConnect, KiteStreamer kiteStreamer, KiteConfig kiteConfig) {
        this.kiteConnect = kiteConnect;
        this.kiteStreamer = kiteStreamer;
        this.kiteConfig = kiteConfig;
    }

    @GetMapping("/kite")
    public String loginCallback(@RequestParam("request_token") String requestToken) {
        try {
            System.out.println("🔁 Request token received: " + requestToken);
            System.out.println("🔑 Using API key: " + kiteConnect.getApiKey());

            User user = kiteConnect.generateSession(requestToken, apiSecret);
            if (user == null) return "❌ User session is null.";

            kiteConnect.setAccessToken(user.accessToken);
            kiteConnect.setPublicToken(user.publicToken);

            Files.writeString(Paths.get("kite.access.token"), user.accessToken);
            Files.writeString(Paths.get("kite.public.token"), user.publicToken);

            System.out.println("✅ Tokens saved.");

            // ✅ Now configure and start
            boolean success = kiteConfig.configureAndStart(kiteConnect);
            return success ? "✅ Streaming started." : "⚠️ Token error.";

        } catch (KiteException e) {
            return "❌ KiteException: " + e.message;
        } catch (IOException e) {
            return "❌ IO Exception: " + e.getMessage();
        } catch (Exception e) {
            return "❌ Unexpected error: " + e.getMessage();
        }
    }

}
