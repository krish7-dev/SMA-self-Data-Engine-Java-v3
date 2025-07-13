package com.marketdata.auth;

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

    public KiteLogin(KiteConnect kiteConnect, KiteStreamer kiteStreamer) {
        this.kiteConnect = kiteConnect;
        this.kiteStreamer = kiteStreamer;
    }

    @GetMapping("/kite")
    public String loginCallback(@RequestParam("request_token") String requestToken) {
        try {
            System.out.println("🔁 Request token received: " + requestToken);
            System.out.println("🔑 Using API key: " + kiteConnect.getApiKey());

            User user = kiteConnect.generateSession(requestToken, apiSecret);

            if (user == null) {
                System.out.println("❌ User object is null");
                return "❌ User session is null. Check request_token or apiSecret.";
            }

            String accessToken = user.accessToken;
            String publicToken = user.publicToken;

            kiteConnect.setAccessToken(accessToken);
            kiteConnect.setPublicToken(publicToken);

            Files.writeString(Paths.get("kite.access.token"), accessToken);
            Files.writeString(Paths.get("kite.public.token"), publicToken);

            System.out.println("✅ Access Token: " + accessToken);
            System.out.println("✅ Public Token: " + publicToken);

            // ✅ Start WebSocket streaming now that token is valid
            kiteStreamer.startStreaming();

            return "✅ Login successful. Tokens saved. Streaming started.";

        } catch (KiteException e) {
            System.out.println("❌ KiteException occurred:");
            System.out.println("🧾 Code: " + e.code);
            System.out.println("📄 Message: " + e.message);
            return "❌ KiteException: " + e.message;
        } catch (IOException e) {
            e.printStackTrace();
            return "❌ IO Exception: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Unexpected error: " + e.getMessage();
        }
    }
}
