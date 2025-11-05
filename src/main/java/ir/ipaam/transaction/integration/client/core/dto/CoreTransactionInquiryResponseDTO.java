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
    String transactionCode;
    String transactionDate;
    String transactionStatus;
}
