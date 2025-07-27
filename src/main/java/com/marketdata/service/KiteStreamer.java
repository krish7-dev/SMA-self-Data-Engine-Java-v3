package com.marketdata.service;

import com.marketdata.enums.TickSourceEnum;
import com.marketdata.model.Tick;
import com.marketdata.repository.SymbolQuery;
import com.marketdata.util.TickQueueUtil;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Profile;
import com.zerodhatech.ticker.KiteTicker;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class KiteStreamer {

    private final KiteConnect kiteConnect;
    private final TickQueueUtil tickQueue;
    private final SymbolQuery symbolQuery;

    private final List<String> symbolsToStream = List.of("TCS", "RELIANCE", "ADANIGREEN","NIFTY50");

    public KiteStreamer(KiteConnect kiteConnect, TickQueueUtil tickQueue, SymbolQuery symbolQuery) {
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
//                System.out.println("custom tick : "+myTick);
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

        // Fields directly from Kite Tick
        tick.setMode(kiteTick.getMode());
        tick.setTradable(kiteTick.isTradable());
        tick.setInstrumentToken(kiteTick.getInstrumentToken());
        tick.setLastTradedPrice(kiteTick.getLastTradedPrice());
        tick.setHighPrice(kiteTick.getHighPrice());
        tick.setLowPrice(kiteTick.getLowPrice());
        tick.setOpenPrice(kiteTick.getOpenPrice());
        tick.setClosePrice(kiteTick.getClosePrice());
        tick.setChange(kiteTick.getChange());

        // Per-trade volume (instant) and cumulative day volume
        tick.setLastTradedQuantity(kiteTick.getLastTradedQuantity());
        tick.setInstantVolume(kiteTick.getLastTradedQuantity());  // Explicit per-tick volume
        tick.setCumulativeVolume(kiteTick.getVolumeTradedToday()); // Running total

        tick.setAverageTradePrice(kiteTick.getAverageTradePrice());
        tick.setTotalBuyQuantity(kiteTick.getTotalBuyQuantity());
        tick.setTotalSellQuantity(kiteTick.getTotalSellQuantity());

        tick.setLastTradedTime(
                kiteTick.getLastTradedTime() != null
                        ? kiteTick.getLastTradedTime().toInstant()
                        : null
        );

        tick.setOi(kiteTick.getOi());
        tick.setOpenInterestDayHigh(kiteTick.getOpenInterestDayHigh());
        tick.setOpenInterestDayLow(kiteTick.getOpenInterestDayLow());

        tick.setTickTimestamp(
                kiteTick.getTickTimestamp() != null
                        ? kiteTick.getTickTimestamp().toInstant()
                        : java.time.Instant.now()
        );

//        tick.setDepth(kiteTick.getMarketDepth());

        // Reverse lookup for symbol (token → name mapping)
        String symbol = symbolQuery.getInstrumentNameFromToken(
                String.valueOf(kiteTick.getInstrumentToken())
        );
        tick.setSymbol(symbol != null ? symbol : "UNKNOWN");

        tick.setExchange("NSE"); // Assuming NSE for all, adjust if needed
        tick.setSource(TickSourceEnum.kite);

        return tick;
    }
}
