package com.cptneemoo;

import com.cptneemoo.exception.ConnectionIOException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionLogger {

    private static final String projectPath = System.getProperty("user.dir");

    private static final String fileSeparator = System.getProperty("file.separator");

    private static final String logFilePath = String.format("%s%slog%slog.txt",
            projectPath, fileSeparator, fileSeparator);

    static void writeConnection(Connection connection, boolean append) throws ConnectionIOException {
        try (FileWriter fr = new FileWriter(logFilePath, append)) {
            fr.write(String.format("%d %d %s\n"
                    , connection.getTime()
                    , connection.getSession()
                    , connection.getIp()));
        } catch (IOException e) {
            throw new ConnectionIOException("Can't write in log.txt");
        }
    }

    static List<Connection> readConnections() throws ConnectionIOException {
        ArrayList<Connection> connections = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
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


    static void filterOldConnections() throws ConnectionIOException {
        long threeDaysAgoInMilliseconds = System.currentTimeMillis() - (86400 * 3 * 1000);
        List<Connection> connections = readConnections();
        try {
            File temp = new File(logFilePath);
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
