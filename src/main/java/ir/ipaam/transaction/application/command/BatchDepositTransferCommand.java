package ir.ipaam.transaction.application.command;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Map;

@Value
@Builder
public class BatchDepositTransferCommand {
    @TargetAggregateIdentifier
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
}
