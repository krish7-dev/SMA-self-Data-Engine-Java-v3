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

    public String getSymbol(String instrument) {
        String sql = "SELECT kite FROM symbols WHERE instrument = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{instrument}, String.class);
        } catch (Exception e) {
            // You can log or handle the error as needed
            return null;
        }
    }
}
