package com.marketdata.db;

import com.marketdata.model.Tick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.marketdata.enums.TickSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class TickQuery {

    private final JdbcTemplate jdbcTemplate;
    private final PartitionManager partitionManager;

    @Autowired
    public TickQuery(JdbcTemplate jdbcTemplate, PartitionManager partitionManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.partitionManager = partitionManager;
    }

    public void save(Tick tick) {
        partitionManager.ensurePartitionExists(tick.getSymbol());

        String sql = "INSERT INTO ticks (symbol, price, volume, timestamp, exchange, source) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tick.getSymbol());
            ps.setDouble(2, tick.getPrice());
            ps.setInt(3, (int) tick.getVolume());
            ps.setTimestamp(4, Timestamp.from(tick.getTimestamp()));
            ps.setString(5, tick.getExchange());
            ps.setString(6,tick.getSource().name());
            return ps;
        });
    }

    public List<Tick> getRecentTicks(String symbol, int limit) {
        String sql = "SELECT * FROM ticks WHERE symbol = ? ORDER BY timestamp DESC LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{symbol, limit}, tickRowMapper);
    }

    public List<Tick> getTicksBetween(String symbol, Instant start, Instant end) {
        String sql = "SELECT * FROM ticks WHERE symbol = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, new Object[]{
                symbol, Timestamp.from(start), Timestamp.from(end)
        }, tickRowMapper);
    }

    private final RowMapper<Tick> tickRowMapper = (ResultSet rs, int rowNum) -> {
        Tick tick = new Tick();
        tick.setSymbol(rs.getString("symbol"));
        tick.setPrice(rs.getDouble("price"));
        tick.setVolume(rs.getInt("volume"));
        tick.setTimestamp(rs.getTimestamp("timestamp").toInstant());
        tick.setExchange(rs.getString("exchange"));

        // ✅ Set source
        String sourceStr = rs.getString("source");
        try {
            tick.setSource(TickSource.valueOf(sourceStr));
        } catch (Exception e) {
            tick.setSource(TickSource.UNKNOWN); // or null if you prefer
        }

        return tick;
    };

}
