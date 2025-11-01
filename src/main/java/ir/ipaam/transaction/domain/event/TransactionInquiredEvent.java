package ir.ipaam.transaction.domain.event;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import lombok.Value;

@Value
public class TransactionInquiredEvent {
    String transactionDate;
    String transactionCode;
//    Integer rsCode;
    TransactionResponseStatus status;
}
