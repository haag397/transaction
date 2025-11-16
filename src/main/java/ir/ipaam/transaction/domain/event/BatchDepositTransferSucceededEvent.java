package ir.ipaam.transaction.domain.event;

import lombok.Value;

@Value
public class BatchDepositTransferSucceededEvent {
    String transactionId;
    String transactionCode;
    String transactionDate;
}