package ir.ipaam.transaction.domain.event;

import ir.ipaam.transaction.domain.model.BatchDepositTransferStatus;
import lombok.Value;

@Value
public class BatchDepositTransferedEvent {
    String transactionDate;
    String transactionId;
    String transactionCode;
    BatchDepositTransferStatus status;
}
