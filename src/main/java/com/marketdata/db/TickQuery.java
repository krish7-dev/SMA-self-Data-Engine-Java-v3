package com.marketdata.db;

import com.marketdata.model.Tick;
import com.marketdata.util.FileTickLogger;
import com.marketdata.enums.TickSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class TickQuery {

    private final JdbcTemplate jdbcTemplate;
    private final PartitionManager partitionManager;
    private final FileTickLogger fileLogger;

    @Autowired
    public TickQuery(JdbcTemplate jdbcTemplate, PartitionManager partitionManager, FileTickLogger fileTickLogger) {
        this.jdbcTemplate = jdbcTemplate;
        this.partitionManager = partitionManager;
        this.fileLogger = fileTickLogger;
    }

    /**
     * Save a tick to the database. Ensures the partition exists, then writes the OHLC tick.
     * Falls back to file logging if the DB write fails.
     */
    public void save(Tick tick) {
        partitionManager.ensurePartitionExists(tick.getSymbol());

        String sql = "INSERT INTO ticks (symbol, timestamp, open, high, low, close, volume, exchange, source) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, tick.getSymbol());
                ps.setTimestamp(2, Timestamp.from(tick.getTimestamp()));
                ps.setDouble(3, tick.getOpenPrice());
                ps.setDouble(4, tick.getHighPrice());
                ps.setDouble(5, tick.getLowPrice());
                ps.setDouble(6, tick.getClosePrice());
                ps.setLong(7, tick.getVolume());
                ps.setString(8, tick.getExchange());
                ps.setString(9, tick.getSource().name());
                return ps;
            });
        } catch (Exception e) {
            System.out.println("Error in writing to DB for tick: " + tick.getSymbol() + " — " + e.getMessage());
            fileLogger.logTick(tick); // Fallback to file if DB fails
        }
    }

    /**
     * Fetch the most recent ticks for a symbol, ordered by timestamp (latest first).
     */
    public List<Tick> getRecentTicks(String symbol, int limit) {
        String sql = "SELECT * FROM ticks WHERE symbol = ? ORDER BY timestamp DESC LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{symbol, limit}, tickRowMapper);
    }

    /**
     * Fetch ticks between two timestamps for a given symbol.
     */
    public List<Tick> getTicksBetween(String symbol, Instant start, Instant end) {
        String sql = "SELECT * FROM ticks WHERE symbol = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, new Object[]{
                symbol, Timestamp.from(start), Timestamp.from(end)
        }, tickRowMapper);
    }

    /**
     * Maps DB rows to Tick objects, handling OHLC values.
     */
    private final RowMapper<Tick> tickRowMapper = (ResultSet rs, int rowNum) -> {
        Tick tick = new Tick();
        tick.setSymbol(rs.getString("symbol"));
        tick.setOpenPrice(rs.getDouble("open"));
        tick.setHighPrice(rs.getDouble("high"));
        tick.setLowPrice(rs.getDouble("low"));
        tick.setClosePrice(rs.getDouble("close"));
        tick.setVolume(rs.getLong("volume"));
        tick.setTimestamp(rs.getTimestamp("timestamp").toInstant());
        tick.setExchange(rs.getString("exchange"));

        // Map source safely
        String sourceStr = rs.getString("source");
        try {
            tick.setSource(TickSource.valueOf(sourceStr));
        } catch (Exception e) {
            tick.setSource(TickSource.UNKNOWN);
        }

        return tick;
    };
}
