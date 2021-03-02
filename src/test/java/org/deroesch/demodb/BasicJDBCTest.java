package org.deroesch.demodb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * Basic JDBC test in Spring
 */
@Slf4j
@SpringBootTest
@Configuration
class BasicJDBCTest {

    @Test
    void testPlainJDBC() throws SQLException {

        final String url = getConnectionString();

        final String stmt = "SELECT id, owner_id, label, email_address FROM public.email_address;";
        log.info("-----------------------------------------------------");
        log.info("Starting");
        try (Connection conn = DriverManager.getConnection(url); PreparedStatement p = conn.prepareStatement(stmt)) {
            log.info("Got connection");

            final ResultSet results = p.executeQuery();

            while (results.next()) {
                final int id = results.getInt(1);
                final int ownerId = results.getInt(2);
                final String label = results.getString(3);
                final String address = results.getString(4);

                log.info(String.format("%s, %s, %s, %s", id, ownerId, label, address));
            }
        }
        log.info("Ending");
        log.info("-----------------------------------------------------");
    }

    /**
     * @return An instantiated connection string.
     */
    String getConnectionString() {
        final String str = "jdbc:postgresql://%s:%s/%s?user=%s&password=%s&ssl=false";
        final String url = String.format(str, host, port, database, user, password);
        return url;
    }

    @Value("${db.host}")
    String host;

    @Value("${db.port}")
    String port;

    @Value("${db.database}")
    String database;

    @Value("${db.user}")
    String user;

    @Value("${db.password}")
    String password;

}
