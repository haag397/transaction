package ir.ipaam.transaction.api.write.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDepositTransferResponseDTO {

    private Result result;
    private Status status;
    private Meta meta;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Result {
        private Data data;
        private Status status;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Data {
        private String transactionDate;
        private String transactionId;
        private String transactionCode;

        private String transactionStatus;     // ← inquiry only
        private String transactionRefNumber;  // ← inquiry only
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Status {
        private String code;
        private String message;
        private String description;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta {
        private String transactionId;
    }
}

