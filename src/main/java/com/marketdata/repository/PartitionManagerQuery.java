package com.marketdata.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PartitionManagerQuery {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PartitionManagerQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void ensurePartitionExists(String symbol) {
        String tableName = "ticks_" + symbol.toLowerCase();

        // Check if the partition already exists
        String existsQuery = "SELECT EXISTS (" +
                "SELECT FROM pg_tables WHERE tablename = ?" +
                ")";
        boolean exists = jdbcTemplate.queryForObject(existsQuery, new Object[]{tableName}, Boolean.class);

        if (!exists) {
            // Create partition for the symbol
            String createPartitionSQL = String.format(
                    "CREATE TABLE IF NOT EXISTS %s PARTITION OF ticks FOR VALUES IN ('%s')",
                    tableName, symbol
            );

            jdbcTemplate.execute(createPartitionSQL);
            System.out.println("🧩 Created new partition: " + tableName);
        }
    }
}
