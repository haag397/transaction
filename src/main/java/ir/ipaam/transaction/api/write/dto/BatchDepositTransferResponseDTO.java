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
    private String transactionId;
    private String transactionDate;
    private String transactionCode;
    private String status;
    private String message;
}

