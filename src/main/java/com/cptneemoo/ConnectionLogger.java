package com.cptneemoo;

import com.cptneemoo.exception.ConnectionDeletingException;
import com.cptneemoo.exception.ConnectionReadingException;
import com.cptneemoo.exception.ConnectionWritingException;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConnectionLogger {

    private static Logger log = Logger.getLogger(ConnectionLogger.class.getName());

    private static final String logFilePath = "C:\\projects\\mentor\\ConnectionLogger\\log\\log.txt";

    private static final String tempFilePath = "C:\\projects\\mentor\\ConnectionLogger\\log\\temp.txt";

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
        } catch (ConnectionWritingException | ConnectionReadingException | InterruptedException | ConnectionDeletingException e) {
            log.severe(String.format("Exception of type %s with message %s", e.getClass().getName(), e.getMessage()));
        }
    }

    public static void writeConnection(List<Connection> connections) throws ConnectionWritingException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            for (Connection connection : connections) {
                writer.write(String.format("%d %d %s\n"
                        , connection.getTime()
                        , connection.getSession()
                        , connection.getIp()));
            }
        } catch (IOException e) {
            throw new ConnectionWritingException("Unable to write in log.txt");
        }
    }

    public static List<Connection> readConnection(long startTime, long endTime)
            throws ConnectionReadingException {
        if (endTime < startTime) throw new ConnectionReadingException("endDate is lower than startDate");
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
            throw new ConnectionReadingException("Unable to read from log.txt");
        }
        return connections;
    }


    public static void deleteOldConnection() throws ConnectionDeletingException {
        long threeDaysInMilliseconds = 86400 * 3 * 1000;
        long now = Instant.now().toEpochMilli();
        File logFile = new File(logFilePath);
        File tempFile = new File(tempFilePath);
        try (
                BufferedReader br = new BufferedReader(new FileReader(logFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line = br.readLine();
            while (line != null && !line.equals("")) {
                String[] parts = line.split(" ");
                long time = Long.parseLong(parts[0]);
                if (time > now - threeDaysInMilliseconds) {
                    bw.write(line + "\n");
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            throw new ConnectionDeletingException("Unable to delete");
        }
        if (!logFile.delete()) throw new ConnectionDeletingException("Unable to delete log.txt");
        if (!tempFile.renameTo(logFile)) throw new ConnectionDeletingException("Unable to rename temp.txt to log.txt");
    }
}
