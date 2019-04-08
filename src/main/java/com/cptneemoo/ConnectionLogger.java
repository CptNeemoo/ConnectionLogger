package com.cptneemoo;

import com.cptneemoo.exception.ConnectionIOException;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConnectionLogger {

    private static Logger log = Logger.getLogger(ConnectionLogger.class.getName());

    private static final String projectPath = System.getProperty("user.dir");

    private static final String fileSeparator = System.getProperty("file.separator");

    private static final String logFilePath = String.format("%s%slog%slog.txt", projectPath, fileSeparator, fileSeparator);

    public static void main(String[] args) {
        ArrayList<Connection> connections = new ArrayList<>(10);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        for (int i = 0; i < 10; i++) {
            connections.add(connectionFactory.getConnection());
        }
        try {
            writeConnection(connections);
            Thread.sleep(1000);
            readConnection(0, Long.MAX_VALUE);
            deleteOldConnection();
        } catch (InterruptedException | ConnectionIOException e) {
            log.severe(String.format("Exception of type %s with message %s", e.getClass().getName(), e.getMessage()));
        }
    }

    public static void writeConnection(List<Connection> connections) throws ConnectionIOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            for (Connection connection : connections) {
                writer.write(String.format("%d %d %s\n"
                        , connection.getTime()
                        , connection.getSession()
                        , connection.getIp()));
            }
        } catch (IOException e) {
            ConnectionIOException cio = new ConnectionIOException("Unable to write in log.txt");
            cio.initCause(e);
            throw cio;
        }
    }

    public static List<Connection> readConnection(long startTime, long endTime)
            throws ConnectionIOException {
        if (endTime < startTime) throw new ConnectionIOException("endDate is lower than startDate");
        ArrayList<Connection> connections = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line = br.readLine();
            while (line != null && !line.equals("")) {
                String[] parts = line.split(" ");
                long time = Long.parseLong(parts[0]);
                if (time >= startTime && time <= endTime) {
                    Connection connection = new Connection();
                    connection.setTime(time);
                    connection.setSession(Integer.parseInt(parts[1]));
                    connection.setIp(parts[2]);
                    connections.add(connection);
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            ConnectionIOException cio = new ConnectionIOException("Unable to read from log.txt");
            cio.initCause(e);
            throw cio;
        }
        return connections;
    }


    public static void deleteOldConnection() throws ConnectionIOException {
        long threeDaysInMilliseconds = 86400 * 3 * 1000;
        long now = Instant.now().toEpochMilli();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line = br.readLine();
            while (line != null && !line.equals("")) {
                String[] parts = line.split(" ");
                long time = Long.parseLong(parts[0]);
                if (time > now - threeDaysInMilliseconds) {
                    sb.append(line + System.lineSeparator());
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            ConnectionIOException cio = new ConnectionIOException("Unable to read log.txt");
            cio.initCause(e);
            throw cio;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath,false))){
            bw.write(sb.toString());
        } catch (Exception e){
            ConnectionIOException cio = new ConnectionIOException("Unable to update log.txt");
            cio.initCause(e);
            throw cio;
        }
    }
}
