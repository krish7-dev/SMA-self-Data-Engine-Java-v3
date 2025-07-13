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

    public KiteStreamer(KiteConnect kiteConnect, TickQueue tickQueue, SymbolQuery symbolQuery) {
        this.kiteConnect = kiteConnect;
        this.tickQueue = tickQueue;
        this.symbolQuery = symbolQuery;
    }

    // Call this ONLY after login success
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

        KiteTicker ticker = new KiteTicker(kiteConnect.getApiKey(), kiteConnect.getAccessToken());

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

            List<Long> tokensToSubscribe = List.of(256265L, 260105L);
            ArrayList<Long> tokenList = new ArrayList<>(tokensToSubscribe);

            ticker.subscribe(tokenList);
            ticker.setMode(tokenList, KiteTicker.modeFull);
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

        // ✅ Use SymbolQuery to get symbol from DB
        String symbol = symbolQuery.getSymbol(String.valueOf(token));
        tick.setSymbol(symbol != null ? symbol : "UNKNOWN");
        tick.setExchange("NSE");                  // hardcode or lookup if needed
        tick.setSource(TickSource.kite);          // set your enum value

        return tick;
    }

}
