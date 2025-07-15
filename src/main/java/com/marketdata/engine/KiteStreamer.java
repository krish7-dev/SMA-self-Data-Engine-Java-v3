package com.marketdata.engine;

import com.marketdata.db.SymbolQuery;
import com.marketdata.enums.TickSource;
import com.marketdata.model.Tick;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Profile;
import com.zerodhatech.ticker.KiteTicker;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class KiteStreamer {

    private final KiteConnect kiteConnect;
    private final TickQueue tickQueue;
    private final SymbolQuery symbolQuery;

    // ✅ Add the list of symbols you want to stream here
    private final List<String> symbolsToStream = List.of("TCS", "RELIANCE", "ADANIGREEN","NIFTY50");
//    private final List<String> symbolsToStream = List.of("TCS");

    public KiteStreamer(KiteConnect kiteConnect, TickQueue tickQueue, SymbolQuery symbolQuery) {
        this.kiteConnect = kiteConnect;
        this.tickQueue = tickQueue;
        this.symbolQuery = symbolQuery;
    }

    public void startStreaming() {
        try {
            if (kiteConnect.getAccessToken() == null) {
                System.out.println("⚠️ Access token not available. Login first.");
                return;
            }

            // Validate session
            Profile profile = kiteConnect.getProfile();
            System.out.println("👤 Logged in as: " + profile.userName);

        } catch (Exception e) {
            System.err.println("❌ Access token invalid or expired: " + e.getMessage());
            return;
        } catch (KiteException e) {
            throw new RuntimeException(e);
        }

        KiteTicker ticker = new KiteTicker(kiteConnect.getAccessToken(),kiteConnect.getApiKey());
        try {
            ticker.setTryReconnection(true);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to enable reconnection: " + e.getMessage());
        }

        ticker.setOnConnectedListener(() -> {
            System.out.println("✅ WebSocket connected!");

            try {
                ticker.setMaximumRetries(-1);
                ticker.setMaximumRetryInterval(15);
            } catch (Exception | KiteException e) {
                System.err.println("⚠️ Failed to set retry params: " + e.getMessage());
            }

            // ✅ Convert symbol list to token list using DB
            ArrayList<Long> tokenList = new ArrayList<>();

            for (String symbol : symbolsToStream) {
                String tokenStr = symbolQuery.getToken(symbol); // <--- this method must be added
                if (tokenStr != null) {
                    try {
                        tokenList.add(Long.parseLong(tokenStr));
                    } catch (NumberFormatException nfe) {
                        System.err.println("❌ Invalid token format for symbol " + symbol + ": " + tokenStr);
                    }
                } else {
                    System.err.println("⚠️ No token found for symbol: " + symbol);
                }
            }

            if (tokenList.isEmpty()) {
                System.err.println("❌ No valid tokens to subscribe to. Streaming aborted.");
                return;
            }

            ticker.subscribe(tokenList);
            ticker.setMode(tokenList, KiteTicker.modeFull);
            System.out.println("📡 Subscribed to tokens: " + tokenList);
        });

        ticker.setOnTickerArrivalListener(ticks -> {
            for (com.zerodhatech.models.Tick kiteTick : ticks) {
                Tick myTick = convertToCustomTick(kiteTick);
                tickQueue.enqueue(myTick);
            }
        });

        ticker.setOnDisconnectedListener(() -> System.out.println("❌ WebSocket disconnected."));

        try {
            ticker.connect();
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to Kite WebSocket: " + e.getMessage());
        }
    }

    private Tick convertToCustomTick(com.zerodhatech.models.Tick kiteTick) {
        Tick tick = new Tick();

        long token = kiteTick.getInstrumentToken();
        tick.setInstrumentToken(token);
        tick.setPrice(kiteTick.getLastTradedPrice());
        tick.setLastTradedQuantity(kiteTick.getLastTradedQuantity());
        tick.setVolume(kiteTick.getVolumeTradedToday());

        tick.setTimestamp(
                kiteTick.getTickTimestamp() != null
                        ? kiteTick.getTickTimestamp().toInstant()
                        : Instant.ofEpochMilli(System.currentTimeMillis())
        );

        // ✅ Reverse lookup: get symbol from token
        String symbol = symbolQuery.getInstrumentNameFromToken(String.valueOf(token));
        tick.setSymbol(symbol != null ? symbol : "UNKNOWN");

        tick.setExchange("NSE");
        tick.setSource(TickSource.kite);

        return tick;
    }
}
