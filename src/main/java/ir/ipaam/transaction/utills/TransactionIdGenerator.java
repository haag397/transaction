package ir.ipaam.transaction.utills;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TransactionIdGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final String PREFIX = "8524";
    private static final String MID = "AeAaxhMq";

    private TransactionIdGenerator() {
    }

    public static String generate() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String base = PREFIX + MID + timestamp;
        long asciiTotal = asciiSum(base);
        return PREFIX + '-' + MID + '-' + timestamp + '-' + asciiTotal;
    }

    private static long asciiSum(String input) {
        long sum = 0;
        for (byte b : input.getBytes(StandardCharsets.US_ASCII)) {
            sum += (b & 0xFF);
        }
        return sum;
    }
}


