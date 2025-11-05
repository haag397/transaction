package ir.ipaam.transaction.integration.client.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorePolTransferResponseDTO {
    private String transactionId;
    private String referenceNumber;
    private String transactionCode;
    private String transactionDate;
    private String status;
    private String message;
}
