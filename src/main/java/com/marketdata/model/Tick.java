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

    public Tick() {}

    public Tick(String symbol, double price, long volume, Instant timestamp, String exchange, TickSource source){
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
        this.timestamp = timestamp;
        this.exchange = exchange;
        this.source = source;
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

    @Override
    public String toString() {
        return "Tick{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", timestamp=" + timestamp +
                ", exchange=" + exchange +
                ", source='" + source + '\''+
                '}';
    }

}
