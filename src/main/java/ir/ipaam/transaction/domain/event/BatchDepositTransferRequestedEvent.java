package ir.ipaam.transaction.domain.event;

import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchDepositTransferRequestedEvent {
    String transactionId;
    UUID id;
    String source;
    String sourceTitle;
    String destination;
    String destinationTitle;
    Long amount;
    String description;
    String sourceDescription;
    String extraDescription;
    String reason;
    Map<String, Object> extraInformation;
    TransactionType type;
    TransactionSubType subType;
}
