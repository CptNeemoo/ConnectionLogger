package com.cptneemoo;

import com.cptneemoo.exception.ConnectionIOException;

import java.util.ArrayList;
import java.util.logging.Logger;

import static com.cptneemoo.ConnectionLogger.*;

public class Main {

    private static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            for (int i = 0; i < 10; i++) {
                writeConnection(connectionFactory.getConnection(), true);
            }
            Connection oldConnection = connectionFactory.getConnection();
            oldConnection.setTime(5555);
            writeConnection(oldConnection, true);
            ArrayList<Connection> connections = (ArrayList<Connection>) readConnections();
            filterOldConnections();
            for (Connection connection : connections){
                log.info(String.format("Connection : %d %d %s",
                        connection.getTime(),
                        connection.getSession(),
                        connection.getIp()));
            }
        } catch (ConnectionIOException e) {
            log.severe(String.format("Exception of type %s with message %s", e.getClass().getName(), e.getMessage()));
        }
    }
}
