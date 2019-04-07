package com.cptneemoo;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionFactoryTest {

    private static final String zeroTo255
            = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    private static final String IP_REGEXP
            = String.format("%s\\.%s\\.%s\\.%s", zeroTo255, zeroTo255, zeroTo255, zeroTo255);

    private static final Pattern IP_PATTERN
            = Pattern.compile(IP_REGEXP);

    private boolean isValid(String address) {
        return IP_PATTERN.matcher(address).matches();
    }

    static int numberLength(long n) {
        if (n == 0) return 1;
        int l;
        n = Math.abs(n);
        for (l = 0; n > 0; ++l)
            n /= 10;
        return l;
    }

    @Test
    void getConnection() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.getConnection();
        assertEquals(Instant.now().toEpochMilli(), connection.getTime(), 1000);
        assertEquals(9, numberLength(connection.getSession()));
        assertTrue(isValid(connection.getIp()));
    }
}