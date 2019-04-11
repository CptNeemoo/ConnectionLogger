package com.cptneemoo.thread;

import com.cptneemoo.exception.ConnectionIOException;
import com.cptneemoo.factory.ConnectionFactory;
import lombok.AllArgsConstructor;

import static com.cptneemoo.service.ConnectionLoggerService.writeConnection;

@AllArgsConstructor
public class LoggingThread extends Thread {

    private String name;

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
