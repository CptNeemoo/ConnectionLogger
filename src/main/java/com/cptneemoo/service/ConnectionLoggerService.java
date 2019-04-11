package com.cptneemoo.service;

import com.cptneemoo.data.Connection;
import com.cptneemoo.exception.ConnectionIOException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionLoggerService {

    private static final String PROJECT_PATH = System.getProperty("user.dir");

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String LOG_FILE_PATH = String.format("%s%slog%slog.txt",
            PROJECT_PATH, FILE_SEPARATOR, FILE_SEPARATOR);

    public static void writeConnection(Connection connection, boolean append) throws ConnectionIOException {
        try (FileWriter fr = new FileWriter(LOG_FILE_PATH, append)) {
            fr.write(String.format("%d %d %s\n"
                    , connection.getTime()
                    , connection.getSession()
                    , connection.getIp()));
        } catch (IOException e) {
            throw new ConnectionIOException("Can't write in log.txt");
        }
    }

    public static List<Connection> readConnections() throws ConnectionIOException {
        ArrayList<Connection> connections = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            String line = br.readLine();
            while (line != null && !line.equals("")) {
                String[] parts = line.split(" ");
                long time = Long.parseLong(parts[0]);
                Connection connection = new Connection(time, Integer.parseInt(parts[1]), parts[2]);
                connections.add(connection);
                line = br.readLine();
            }
        } catch (Exception e) {
            ConnectionIOException cio = new ConnectionIOException("Unable to read from log.txt");
            cio.initCause(e);
            throw cio;
        }
        return connections;
    }


    public static void filterOldConnections() throws ConnectionIOException {
        long threeDaysAgoInMilliseconds = System.currentTimeMillis() - (86400 * 3 * 1000);
        List<Connection> connections = readConnections();
        try {
            File temp = new File(LOG_FILE_PATH);
            if (temp.exists()) {
                RandomAccessFile raf = new RandomAccessFile(temp, "rw");
                raf.setLength(0);
            }
        } catch (Exception e) {
            throw new ConnectionIOException("Can't clear log.txt");
        }
        for (Connection connection : connections) {
            if (connection.getTime() > threeDaysAgoInMilliseconds) {
                writeConnection(connection, true);
            }
        }
    }
}
