package ir.ipaam.transaction.domain.event;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class BatchDepositTransferedEvent {
    String transactionId;
    String source;
    String sourceTitle;
    String destination;
    String destinationTitle;
    Long amount;
    String description;
    String sourceDescription;
    String extraDescription;
    Map<String, Object> extraInformation;
    String reason;
    String transactionCode;
    String transactionDate;
    TransactionResponseStatus status;
//    TransactionType type;
//    TransactionSubType subType;
}
