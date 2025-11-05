package ir.ipaam.transaction.api.write.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatnaTransferResponseDTO {
    private String transactionId;
    private String transactionDate;
    private Long amount;
    private String receiverName;
    private String receiverLastName;
    private String destinationDepNum;
    private String userReferenceNumber;
    private String transactionCode;
    private String status;
    private String message;
}


