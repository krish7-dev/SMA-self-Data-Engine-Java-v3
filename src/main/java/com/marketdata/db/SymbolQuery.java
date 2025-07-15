package com.marketdata.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SymbolQuery {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SymbolQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🔁 Get token from symbol name
    public String getToken(String symbol) {
        String sql = "SELECT kite FROM symbols WHERE instrument = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{symbol}, String.class);
        } catch (Exception e) {
            System.err.println("⚠️ Symbol not found: " + symbol);
            return null;
        }
    }

    // 🔁 Get symbol name from token
    public String getInstrumentNameFromToken(String token) {
        String sql = "SELECT instrument FROM symbols WHERE kite = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{token}, String.class);
        } catch (Exception e) {
            System.err.println("⚠️ Token not found: " + token);
            return null;
        }
    }
}
