package ir.ipaam.transaction.integration.client.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
    public class CoreBatchDepositTransferResponseDTO {

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
