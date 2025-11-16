package ir.ipaam.transaction.utills;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionIdGenerator {

    private static final String PREFIX = "8524";
    private static final String MID = "AeAaxhMq";

    public static String generate() {

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        int asciiSum = (PREFIX + MID + timestamp)
                .chars()
                .sum();

        return PREFIX + "-" + MID + "-" + timestamp + "-" + asciiSum;
    }
}


