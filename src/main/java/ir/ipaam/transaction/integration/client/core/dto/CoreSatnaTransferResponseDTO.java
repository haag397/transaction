package ir.ipaam.transaction.integration.client.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreSatnaTransferResponseDTO {
    private String transactionId;
    private String transactionDate;
    private Long amount;
    private String recieverName;
    private String recieverlastName;
    private String destinationDepNum;
    private String userReferenceNumber;
    private String transactionCode;
}


