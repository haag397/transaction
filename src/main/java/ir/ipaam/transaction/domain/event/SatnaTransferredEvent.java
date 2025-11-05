package ir.ipaam.transaction.domain.event;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SatnaTransferredEvent {
    String transactionId;
    String transactionDate;
    String transactionCode;
    Long amount;
    String receiverName;
    String receiverLastName;
    String destinationDepNum;
    String sourceDepNum;
    String userReferenceNumber;
    TransactionResponseStatus transactionResponseStatus;
}

