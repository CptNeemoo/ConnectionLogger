package com.cptneemoo;

import com.cptneemoo.exception.ConnectionIOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionLoggerTest {

    private static final String projectPath = System.getProperty("user.dir");

    private static final String fileSeparator = System.getProperty("file.separator");

    private static final String logFilePath = String.format("%s%slog%slog.txt"
            , projectPath, fileSeparator, fileSeparator);

    private static String logFileContent;

    @BeforeAll
    static void setUp() throws ConnectionIOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append(System.lineSeparator());
                line = br.readLine();
            }
        } catch (Exception e) {
            throw new ConnectionIOException("Can't read log.txt");
        }
        logFileContent = sb.toString();
    }

    @Test
    void connectionTest() throws ConnectionIOException {
        Connection connection = new Connection(System.currentTimeMillis(), 123456789, "123.123.123.123");
        ConnectionLogger.writeConnection(connection, true);
        List<Connection> connections = ConnectionLogger.readConnections();
        assertTrue(connections.contains(connection));
    }

    @Test
    void filterOldConnectionsTest() throws ConnectionIOException {
        Connection connection = new ConnectionFactory().getConnection();
        connection.setTime(new Random().nextInt(10000));
        ConnectionLogger.writeConnection(connection, true);
        List<Connection> oldConnections = ConnectionLogger.readConnections();
        ConnectionLogger.filterOldConnections();
        List<Connection> newConnections = ConnectionLogger.readConnections();
        assertFalse(newConnections.contains(connection));
        assertTrue(oldConnections.containsAll(newConnections));
    }

    @AfterAll
    static void cleanUp() throws ConnectionIOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath, false))) {
            bw.write(logFileContent);
        } catch (Exception e) {
            throw new ConnectionIOException("Can't write to log.txt");
        }
    }
}