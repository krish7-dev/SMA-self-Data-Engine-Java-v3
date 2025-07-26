package com.marketdata.db;

import com.marketdata.model.Tick;
import com.marketdata.enums.TickSource;
import com.zerodhatech.models.Depth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TickQuery {

    private final JdbcTemplate jdbcTemplate;
    private final PartitionManager partitionManager;

    @Autowired
    public TickQuery(JdbcTemplate jdbcTemplate, PartitionManager partitionManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.partitionManager = partitionManager;
    }

    /**
     * Save a tick to the database. Ensures the partition exists, then writes the full tick.
     */
    public void save(Tick tick) {
        partitionManager.ensurePartitionExists(tick.getSymbol());

        String sql = """
            INSERT INTO ticks (
                symbol, tick_timestamp, instrument_token, last_traded_price,
                open_price, high_price, low_price, close_price, change,
                last_traded_quantity, instant_volume, cumulative_volume,
                average_trade_price, total_buy_quantity, total_sell_quantity,
                last_traded_time, oi, open_interest_day_high, open_interest_day_low,
                exchange, source
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                int idx = 1;
                ps.setString(idx++, tick.getSymbol());
                ps.setTimestamp(idx++, Timestamp.from(tick.getTickTimestamp()));
                ps.setLong(idx++, tick.getInstrumentToken());
                ps.setDouble(idx++, tick.getLastTradedPrice());
                ps.setDouble(idx++, tick.getOpenPrice());
                ps.setDouble(idx++, tick.getHighPrice());
                ps.setDouble(idx++, tick.getLowPrice());
                ps.setDouble(idx++, tick.getClosePrice());
                ps.setDouble(idx++, tick.getChange());
                ps.setDouble(idx++, tick.getLastTradedQuantity());
                ps.setDouble(idx++, tick.getInstantVolume());
                ps.setLong(idx++, tick.getCumulativeVolume());
                ps.setDouble(idx++, tick.getAverageTradePrice());
                ps.setDouble(idx++, tick.getTotalBuyQuantity());
                ps.setDouble(idx++, tick.getTotalSellQuantity());
                ps.setTimestamp(idx++, tick.getLastTradedTime() != null ? Timestamp.from(tick.getLastTradedTime()) : null);
                ps.setDouble(idx++, tick.getOi());
                ps.setDouble(idx++, tick.getOpenInterestDayHigh());
                ps.setDouble(idx++, tick.getOpenInterestDayLow());
                ps.setString(idx++, tick.getExchange());
                ps.setString(idx++, tick.getSource() != null ? tick.getSource().name() : "UNKNOWN");
                return ps;
            });
        } catch (Exception e) {
            System.out.println("Error writing tick for " + tick.getSymbol() + ": " + e.getMessage());
            // Optional fallback: log to file if DB fails
        }
    }

    /**
     * Fetch the most recent ticks for a symbol, ordered by tick_timestamp (latest first).
     */
    public List<Tick> getRecentTicks(String symbol, int limit) {
        String sql = "SELECT * FROM ticks WHERE symbol = ? ORDER BY tick_timestamp DESC LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{symbol, limit}, tickRowMapper);
    }

    /**
     * Fetch ticks between two timestamps for a given symbol.
     */
    public List<Tick> getTicksBetween(String symbol, Instant start, Instant end) {
        String sql = """
            SELECT * FROM ticks 
            WHERE symbol = ? AND tick_timestamp BETWEEN ? AND ? 
            ORDER BY tick_timestamp ASC
        """;
        return jdbcTemplate.query(sql, new Object[]{
                symbol, Timestamp.from(start), Timestamp.from(end)
        }, tickRowMapper);
    }

    /**
     * Maps DB rows to Tick objects.
     */
    private final RowMapper<Tick> tickRowMapper = (ResultSet rs, int rowNum) -> {
        Tick tick = new Tick();
        tick.setSymbol(rs.getString("symbol"));
        tick.setTickTimestamp(rs.getTimestamp("tick_timestamp").toInstant());
        tick.setInstrumentToken(rs.getLong("instrument_token"));
        tick.setLastTradedPrice(rs.getDouble("last_traded_price"));
        tick.setOpenPrice(rs.getDouble("open_price"));
        tick.setHighPrice(rs.getDouble("high_price"));
        tick.setLowPrice(rs.getDouble("low_price"));
        tick.setClosePrice(rs.getDouble("close_price"));
        tick.setChange(rs.getDouble("change"));
        tick.setLastTradedQuantity(rs.getDouble("last_traded_quantity"));
        tick.setInstantVolume(rs.getDouble("instant_volume"));
        tick.setCumulativeVolume(rs.getLong("cumulative_volume"));
        tick.setAverageTradePrice(rs.getDouble("average_trade_price"));
        tick.setTotalBuyQuantity(rs.getDouble("total_buy_quantity"));
        tick.setTotalSellQuantity(rs.getDouble("total_sell_quantity"));
        if (rs.getTimestamp("last_traded_time") != null) {
            tick.setLastTradedTime(rs.getTimestamp("last_traded_time").toInstant());
        }
        tick.setOi(rs.getDouble("oi"));
        tick.setOpenInterestDayHigh(rs.getDouble("open_interest_day_high"));
        tick.setOpenInterestDayLow(rs.getDouble("open_interest_day_low"));



        tick.setExchange(rs.getString("exchange"));
        String sourceStr = rs.getString("source");
        try {
            tick.setSource(TickSource.valueOf(sourceStr));
        } catch (Exception e) {
            tick.setSource(TickSource.UNKNOWN);
        }
        return tick;
    };
}
