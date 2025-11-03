package ir.ipaam.transaction.domain.event;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BatchDepositTransferedEvent {
    String transactionId;
    String transactionDate;
    String transactionCode;
    TransactionResponseStatus transactionResponseStatus;
}
