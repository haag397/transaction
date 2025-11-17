package ir.ipaam.transaction.api.write.dto;

import ir.ipaam.transaction.domain.model.TransactionSubType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDepositTransferRequestDTO {
    String source;
    String description;
    String sourceDescription;
    String transferBillNumber;
    String destination;
    String reason;
    Long amount;
    TransactionSubType subType;
}

