package com.cptneemoo;

import com.cptneemoo.exception.ConnectionIOException;
import com.cptneemoo.thread.LoggingThread;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            LoggingThread lt1 = new LoggingThread("Thread " + 1);
            LoggingThread lt2 = new LoggingThread("Thread " + 2);
            LoggingThread lt3 = new LoggingThread("Thread " + 3);
            List<LoggingThread> loggingThreadList = new ArrayList<>(3);
            loggingThreadList.add(lt1);
            loggingThreadList.add(lt2);
            loggingThreadList.add(lt3);
            for (int i = 0; i < 3; i++) {
                for (LoggingThread lt: loggingThreadList) {
                    lt.run();
                }
                try {
                    Thread.sleep(180000);
                } catch (InterruptedException e) {
                    throw new ConnectionIOException("Interrupted main thread");
                }
            }
        } catch (ConnectionIOException e) {
            log.severe(String.format("Exception of type %s with message %s", e.getClass().getName(), e.getMessage()));
        }
    }
}
