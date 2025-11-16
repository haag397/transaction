package ir.ipaam.transaction.domain.event;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import lombok.Value;

@Value
public class BatchDepositTransferInquiredEvent {
    String transactionId;
    String transactionCode;
    String transactionDate;
    TransactionResponseStatus status;
    String refNumber;
}
