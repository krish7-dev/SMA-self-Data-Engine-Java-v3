package com.marketdata.util;

import com.marketdata.engine.KiteStreamer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StreamStarter {

    private final KiteStreamer kiteStreamer;

    public StreamStarter(KiteStreamer kiteStreamer) {
        this.kiteStreamer = kiteStreamer;
    }

    @PostConstruct
    public void startStreamIfTokenPresent() {
        System.out.println("🟢 StreamStarter initialized. Starting KiteStreamer...");
        kiteStreamer.startStreaming();
    }
}
