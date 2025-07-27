package com.marketdata.model;

import com.marketdata.enums.TickSourceEnum;
import com.zerodhatech.models.Depth;
import java.time.Instant;

public class Tick {

    // Fields from Kite Tick
    private String mode;
    private boolean tradable;
    private long instrumentToken;
    private double lastTradedPrice;
    private double highPrice;
    private double lowPrice;
    private double openPrice;
    private double closePrice;
    private double change;
    private double lastTradedQuantity;   // raw trade size from Kite
    private double instantVolume;        // per-tick volume (same as lastTradedQuantity, explicit for ML)
    private long cumulativeVolume;       // volumeTradedToday from Kite (running total)
    private double averageTradePrice;
    private double totalBuyQuantity;
    private double totalSellQuantity;
    private Instant lastTradedTime;
    private double oi;
    private double openInterestDayHigh;
    private double openInterestDayLow;
    private Instant tickTimestamp;

    // Our additional fields
    private String symbol;
    private String exchange;
    private TickSourceEnum source;

    // Default constructor
    public Tick() {}

    // All-fields constructor
    public Tick(String mode, boolean tradable, long instrumentToken, double lastTradedPrice, double highPrice,
                double lowPrice, double openPrice, double closePrice, double change, double lastTradedQuantity,
                double instantVolume, long cumulativeVolume, double averageTradePrice, double totalBuyQuantity,
                double totalSellQuantity, Instant lastTradedTime, double oi, double openInterestDayHigh,
                double openInterestDayLow, Instant tickTimestamp,
                String symbol, String exchange, TickSourceEnum source) {

        this.mode = mode;
        this.tradable = tradable;
        this.instrumentToken = instrumentToken;
        this.lastTradedPrice = lastTradedPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.change = change;
        this.lastTradedQuantity = lastTradedQuantity;
        this.instantVolume = instantVolume;
        this.cumulativeVolume = cumulativeVolume;
        this.averageTradePrice = averageTradePrice;
        this.totalBuyQuantity = totalBuyQuantity;
        this.totalSellQuantity = totalSellQuantity;
        this.lastTradedTime = lastTradedTime;
        this.oi = oi;
        this.openInterestDayHigh = openInterestDayHigh;
        this.openInterestDayLow = openInterestDayLow;
        this.tickTimestamp = tickTimestamp;
        this.symbol = symbol;
        this.exchange = exchange;
        this.source = source;
    }

    // Getters & Setters
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public boolean isTradable() { return tradable; }
    public void setTradable(boolean tradable) { this.tradable = tradable; }

    public long getInstrumentToken() { return instrumentToken; }
    public void setInstrumentToken(long instrumentToken) { this.instrumentToken = instrumentToken; }

    public double getLastTradedPrice() { return lastTradedPrice; }
    public void setLastTradedPrice(double lastTradedPrice) { this.lastTradedPrice = lastTradedPrice; }

    public double getHighPrice() { return highPrice; }
    public void setHighPrice(double highPrice) { this.highPrice = highPrice; }

    public double getLowPrice() { return lowPrice; }
    public void setLowPrice(double lowPrice) { this.lowPrice = lowPrice; }

    public double getOpenPrice() { return openPrice; }
    public void setOpenPrice(double openPrice) { this.openPrice = openPrice; }

    public double getClosePrice() { return closePrice; }
    public void setClosePrice(double closePrice) { this.closePrice = closePrice; }

    public double getChange() { return change; }
    public void setChange(double change) { this.change = change; }

    public double getLastTradedQuantity() { return lastTradedQuantity; }
    public void setLastTradedQuantity(double lastTradedQuantity) {
        this.lastTradedQuantity = lastTradedQuantity;
        this.instantVolume = lastTradedQuantity;  // Keep in sync
    }

    public double getInstantVolume() { return instantVolume; }
    public void setInstantVolume(double instantVolume) { this.instantVolume = instantVolume; }

    public long getCumulativeVolume() { return cumulativeVolume; }
    public void setCumulativeVolume(long cumulativeVolume) { this.cumulativeVolume = cumulativeVolume; }

    public double getAverageTradePrice() { return averageTradePrice; }
    public void setAverageTradePrice(double averageTradePrice) { this.averageTradePrice = averageTradePrice; }

    public double getTotalBuyQuantity() { return totalBuyQuantity; }
    public void setTotalBuyQuantity(double totalBuyQuantity) { this.totalBuyQuantity = totalBuyQuantity; }

    public double getTotalSellQuantity() { return totalSellQuantity; }
    public void setTotalSellQuantity(double totalSellQuantity) { this.totalSellQuantity = totalSellQuantity; }

    public Instant getLastTradedTime() { return lastTradedTime; }
    public void setLastTradedTime(Instant lastTradedTime) { this.lastTradedTime = lastTradedTime; }

    public double getOi() { return oi; }
    public void setOi(double oi) { this.oi = oi; }

    public double getOpenInterestDayHigh() { return openInterestDayHigh; }
    public void setOpenInterestDayHigh(double openInterestDayHigh) { this.openInterestDayHigh = openInterestDayHigh; }

    public double getOpenInterestDayLow() { return openInterestDayLow; }
    public void setOpenInterestDayLow(double openInterestDayLow) { this.openInterestDayLow = openInterestDayLow; }

    public Instant getTickTimestamp() { return tickTimestamp; }
    public void setTickTimestamp(Instant tickTimestamp) { this.tickTimestamp = tickTimestamp; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public TickSourceEnum getSource() { return source; }
    public void setSource(TickSourceEnum source) { this.source = source; }

    @Override
    public String toString() {
        return "Tick{" +
                "symbol='" + symbol + '\'' +
                ", mode='" + mode + '\'' +
                ", tradable=" + tradable +
                ", instrumentToken=" + instrumentToken +
                ", lastTradedPrice=" + lastTradedPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", openPrice=" + openPrice +
                ", closePrice=" + closePrice +
                ", change=" + change +
                ", lastTradedQuantity=" + lastTradedQuantity +
                ", instantVolume=" + instantVolume +
                ", cumulativeVolume=" + cumulativeVolume +
                ", averageTradePrice=" + averageTradePrice +
                ", totalBuyQuantity=" + totalBuyQuantity +
                ", totalSellQuantity=" + totalSellQuantity +
                ", lastTradedTime=" + lastTradedTime +
                ", oi=" + oi +
                ", openInterestDayHigh=" + openInterestDayHigh +
                ", openInterestDayLow=" + openInterestDayLow +
                ", tickTimestamp=" + tickTimestamp +
                ", exchange='" + exchange + '\'' +
                ", source=" + source +
                '}';
    }
}