package com.cptneemoo.factory;

import com.cptneemoo.data.Connection;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Random;

public class ConnectionFactory {

    private static Random random = new Random();

    public static Connection getConnection() {
        return new Connection(Instant.now().toEpochMilli()
                , nextIntFromRange(100000000, 999999999)
                , generateIpv4());
    }

    private static int nextIntFromRange(int start, int end) {
        if (end < start) throw new IllegalArgumentException("Inappropriate arguments");
        return start + random.nextInt(end - start);
    }

    private static String generateIpv4() {
        return MessageFormat.format("{0}.{1}.{2}.{3}",
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256));
    }
}
