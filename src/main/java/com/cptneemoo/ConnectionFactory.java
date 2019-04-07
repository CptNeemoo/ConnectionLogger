package com.cptneemoo;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Random;

class ConnectionFactory {

    private Random random = new Random();

    Connection getConnection() {
        Connection connection = new Connection();
        connection.setTime(Instant.now().toEpochMilli());
        connection.setSession(nextIntFromRange(100000000, 999999999));
        connection.setIp(generateIpv4());
        return connection;
    }

    private int nextIntFromRange(int start, int end) {
        if (end < start) throw new IllegalArgumentException("Inappropriate arguments");
        return start + random.nextInt(end - start);
    }

    private String generateIpv4() {
        return MessageFormat.format("{0}.{1}.{2}.{3}",
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256));
    }
}
