package ir.ipaam.transaction.integration.client.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreTransactionInquiryResponseDTO {
//    private Integer rsCode;
//    private ResultData resultData;
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class ResultData {
//        private String transactionCode;
//        private String transactionDate;
//        private String transactionStatus;
//    }
    String transactionCode;
    String transactionDate;
    String transactionStatus;
}
