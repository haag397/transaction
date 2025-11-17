package ir.ipaam.transaction.domain.event;

import lombok.Value;

@Value
public class BatchDepositTransferFailedEvent {
    String transactionId;
}
