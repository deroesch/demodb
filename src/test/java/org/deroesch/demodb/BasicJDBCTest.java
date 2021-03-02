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

    @Autowired
    private ApplicationContext context;

    //
    // The @Value elements below come from application.properties
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
        // Have to do this initialization at the instance level or @Value fetches won't
        // work.
        //
        if (null == url) {
            final String str = "jdbc:postgresql://%s:%s/%s?user=%s&password=%s&ssl=false";
            url = String.format(str, host, port, database, user, password);
        }
    }

    /**
     * Test a simple query.
     * 
     * @throws SQLException
     */
    @Test
    void testPlainJDBC() throws SQLException {

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
     * Test a more complex query read from a file.
     * 
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Test
    void testBigQuery() throws SQLException, FileNotFoundException, IOException {

        String stmt = "empty";
        final Resource resource = context.getResource(String.format("classpath:queries/getStreetAddresses.sql"));
        try (BufferedInputStream bis = new BufferedInputStream(resource.getInputStream())) {

            // Read the whole query from the file.
            stmt = new String(bis.readAllBytes());
        }

        log.info("-----------------------------------------------------");
        log.info("Starting");
        try (Connection conn = DriverManager.getConnection(url); PreparedStatement p = conn.prepareStatement(stmt)) {
            log.info("Got connection");

            final ResultSet results = p.executeQuery();

            while (results.next()) {
                int i = 1;
                final String lastName = results.getString(i++);
                final String firstName = results.getString(i++);
                final String middleName = results.getString(i++);
                final String label = results.getString(i++);
                final String addr1 = results.getString(i++);
                final String addr2 = results.getString(i++);
                final String city = results.getString(i++);
                final String state = results.getString(i++);
                final int zip_code = results.getInt(i++);

                final String msg = String.format("%s %s %s %s %s %s %s %s %s", lastName, firstName, middleName, label,
                        addr1, addr2, city, state, zip_code);
                log.info(msg);
            }
        }
        log.info("Ending");
        log.info("-----------------------------------------------------");

    }

}
