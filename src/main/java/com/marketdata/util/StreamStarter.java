package com.marketdata.util;

import com.marketdata.engine.KiteStreamer;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class StreamStarter {

    private final KiteStreamer kiteStreamer;
    private final KiteConnect kiteConnect;

    public StreamStarter(KiteConnect kiteConnect, KiteStreamer  kiteStreamer) {
        this.kiteConnect = kiteConnect;
        this.kiteStreamer = kiteStreamer;
    }

    @PostConstruct
    public void startStreamIfTokenPresent() {
        try {
            // If getProfile works, token is valid — start streaming
            kiteConnect.getProfile(); // This will throw if token is invalid
            System.out.println("✅ Valid token found");
            System.out.println("🟢 StreamStarter initialized. Starting KiteStreamer...");
            kiteStreamer.startStreaming();
        } catch (KiteException | IOException e) {
            System.out.println("⏸️ Skipping streaming — login required.");
        }
    }
}
