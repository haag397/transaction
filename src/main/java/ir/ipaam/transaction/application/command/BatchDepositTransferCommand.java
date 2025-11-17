package ir.ipaam.transaction.application.command;

import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class BatchDepositTransferCommand {
    @TargetAggregateIdentifier
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
