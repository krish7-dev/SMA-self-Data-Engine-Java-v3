package com.marketdata.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TokenQuery {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TokenQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getLatestAccessToken() {
        String sql = "SELECT access_token FROM tokens ORDER BY created_at DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, String.class);
    }

    public String getLatestPublicToken() {
        String sql = "SELECT public_token FROM tokens ORDER BY created_at DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, String.class);
    }

    // Function to insert or update tokens (always keep 1 row)
    public void saveTokens(String accessToken, String publicToken) {
        String sql = """
        INSERT INTO tokens (id, access_token, public_token, created_at)
        VALUES (1, ?, ?, NOW())
        ON CONFLICT (id)
        DO UPDATE SET access_token = EXCLUDED.access_token,
                      public_token = EXCLUDED.public_token,
                      created_at = NOW();
        """;
        jdbcTemplate.update(sql, accessToken, publicToken);
    }

}
