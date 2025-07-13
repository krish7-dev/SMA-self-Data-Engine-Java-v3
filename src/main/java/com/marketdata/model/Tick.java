package com.marketdata.model;

import com.marketdata.enums.TickSource;

import java.time.Instant;

public class Tick {

    private String symbol;
    private double price;
    private long volume;
    private Instant timestamp;
    private String exchange;
    private TickSource source;

    private long instrumentToken;
    private double lastTradedQuantity;
    private double averageTradePrice;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double change;
    private long totalBuyQuantity;
    private long totalSellQuantity;
    private double oi;
    private double openInterestDayHigh;
    private double openInterestDayLow;

    public Tick() {}

    public Tick(String symbol, double price, long volume, Instant timestamp, String exchange, TickSource source,
                long instrumentToken, double lastTradedQuantity, double averageTradePrice, double openPrice,
                double highPrice, double lowPrice, double closePrice, double change, long totalBuyQuantity,
                long totalSellQuantity, double oi, double openInterestDayHigh, double openInterestDayLow) {
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
        this.timestamp = timestamp;
        this.exchange = exchange;
        this.source = source;
        this.instrumentToken = instrumentToken;
        this.lastTradedQuantity = lastTradedQuantity;
        this.averageTradePrice = averageTradePrice;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.change = change;
        this.totalBuyQuantity = totalBuyQuantity;
        this.totalSellQuantity = totalSellQuantity;
        this.oi = oi;
        this.openInterestDayHigh = openInterestDayHigh;
        this.openInterestDayLow = openInterestDayLow;
    }


    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public TickSource getSource() { return source; }
    public void setSource(TickSource source) { this.source = source; }

    public long getInstrumentToken() { return instrumentToken; }
    public void setInstrumentToken(long instrumentToken) { this.instrumentToken = instrumentToken; }

    public double getLastTradedQuantity() { return lastTradedQuantity; }
    public void setLastTradedQuantity(double lastTradedQuantity) { this.lastTradedQuantity = lastTradedQuantity; }

    public double getAverageTradePrice() { return averageTradePrice; }
    public void setAverageTradePrice(double averageTradePrice) { this.averageTradePrice = averageTradePrice; }

    public double getOpenPrice() { return openPrice; }
    public void setOpenPrice(double openPrice) { this.openPrice = openPrice; }

    public double getHighPrice() { return highPrice; }
    public void setHighPrice(double highPrice) { this.highPrice = highPrice; }

    public double getLowPrice() { return lowPrice; }
    public void setLowPrice(double lowPrice) { this.lowPrice = lowPrice; }

    public double getClosePrice() { return closePrice; }
    public void setClosePrice(double closePrice) { this.closePrice = closePrice; }

    public double getChange() { return change; }
    public void setChange(double change) { this.change = change; }

    public long getTotalBuyQuantity() { return totalBuyQuantity; }
    public void setTotalBuyQuantity(long totalBuyQuantity) { this.totalBuyQuantity = totalBuyQuantity; }

    public long getTotalSellQuantity() { return totalSellQuantity; }
    public void setTotalSellQuantity(long totalSellQuantity) { this.totalSellQuantity = totalSellQuantity; }

    public double getOi() { return oi; }
    public void setOi(double oi) { this.oi = oi; }

    public double getOpenInterestDayHigh() { return openInterestDayHigh; }
    public void setOpenInterestDayHigh(double openInterestDayHigh) { this.openInterestDayHigh = openInterestDayHigh; }

    public double getOpenInterestDayLow() { return openInterestDayLow; }
    public void setOpenInterestDayLow(double openInterestDayLow) { this.openInterestDayLow = openInterestDayLow; }

    @Override
    public String toString() {
        return "Tick{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", timestamp=" + timestamp +
                ", exchange='" + exchange + '\'' +
                ", source='" + source + '\'' +
                ", instrumentToken=" + instrumentToken +
                ", lastTradedQuantity=" + lastTradedQuantity +
                ", averageTradePrice=" + averageTradePrice +
                ", openPrice=" + openPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", closePrice=" + closePrice +
                ", change=" + change +
                ", totalBuyQuantity=" + totalBuyQuantity +
                ", totalSellQuantity=" + totalSellQuantity +
                ", oi=" + oi +
                ", openInterestDayHigh=" + openInterestDayHigh +
                ", openInterestDayLow=" + openInterestDayLow +
                '}';
    }
}
