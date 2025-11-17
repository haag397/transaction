package ir.ipaam.transaction.utills;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.SecureRandom;

import static org.bouncycastle.pqc.legacy.math.ntru.polynomial.DenseTernaryPolynomial.generateRandom;

public class TransactionIdGenerator {

    private static final String PREFIX = "8524";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generate() {
        String mid = generateRandom(8);
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        int asciiSum = (PREFIX + mid + timestamp)
                .chars()
                .sum();

        return PREFIX + "-" + mid + "-" + timestamp + "-" + asciiSum;
    }

    private static String generateRandom(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
}


