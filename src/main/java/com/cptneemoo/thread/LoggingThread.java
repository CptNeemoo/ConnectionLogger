package com.cptneemoo.thread;

import com.cptneemoo.exception.ConnectionIOException;
import com.cptneemoo.factory.ConnectionFactory;

import static com.cptneemoo.service.ConnectionLoggerService.writeConnection;

public class LoggingThread extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                writeConnection(ConnectionFactory.getConnection(), true);
            } catch (ConnectionIOException e) {
                e.printStackTrace();
            }
        }
    }
}
