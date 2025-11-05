package ir.ipaam.transaction.api.write.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolTransferResponseDTO {
    private String transactionId;
    private String referenceNumber;
    private String status;
    private String message;
}
