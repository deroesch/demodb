package org.deroesch.demodb;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * Basic JDBC test in Spring
 */
@Slf4j
@SpringBootTest
@Configuration
class BasicJDBCTest {

    /**
     * Test a simple query.
     * 
     * @throws SQLException
     */
    @Test
    void testPlainJDBC() throws SQLException {

        log.info("-----------------------------------------------------");
        log.info("Starting");
        final String stmt = "SELECT id, owner_id, label, email_address FROM public.email_address;";
        try (Connection conn = DriverManager.getConnection(url); PreparedStatement ps = conn.prepareStatement(stmt)) {
            log.info("Got connection");

            final ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int i = 1;
                final int id = rs.getInt(i++);
                final int ownerId = rs.getInt(i++);
                final String label = rs.getString(i++);
                final String address = rs.getString(i++);

                log.info(String.format("%s, %s, %s, %s", id, ownerId, label, address));
            }
        }
        log.info("Ending");
        log.info("-----------------------------------------------------");
    }

    /**
     * Test a more complex query whose text is read from a file.
     * 
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Test
    void testBigQuery() throws SQLException, FileNotFoundException, IOException {

        log.info("-----------------------------------------------------");
        log.info("Starting");

        String stmt = "empty";
        final Resource resource = context.getResource(String.format("classpath:queries/getStreetAddresses.sql"));
        try (BufferedInputStream bis = new BufferedInputStream(resource.getInputStream())) {

            // Read the whole query from the file.
            stmt = new String(bis.readAllBytes());
        }

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement ps = conn.prepareStatement(stmt)) {
            log.info("Got connection");

            final ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int i = 1;
                final String lastName = rs.getString(i++);
                final String firstName = rs.getString(i++);
                final String label = rs.getString(i++);
                final String addr1 = rs.getString(i++);
                final String addr2 = rs.getString(i++);
                final String city = rs.getString(i++);
                final String state = rs.getString(i++);
                final int zip_code = rs.getInt(i++);
                final String w3w = rs.getString(i++);

                final String msg = String.format("%s %s %s %s %s %s %s %s %s", lastName, firstName, label, addr1, addr2,
                        city, state, zip_code, w3w);
                log.info(msg);
            }
        }
        log.info("Ending");
        log.info("-----------------------------------------------------");

    }

    @Autowired
    private ApplicationContext context;

    //
    // The @Value elements below come from the application.properties file
    //

    @Value("${db.host}")
    private String host;

    @Value("${db.port}")
    private String port;

    @Value("${db.database}")
    private String database;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    // Our JDBC connection string
    private String url;

    @BeforeEach
    void init() {
        //
        // We have to do field initialization at the instance level or the @Value
        // fetches don't work.
        //
        if (null == url) {
            final String str = "jdbc:postgresql://%s:%s/%s?user=%s&password=%s&ssl=false";
            url = String.format(str, host, port, database, user, password);
        }
    }

}
