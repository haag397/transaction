package ir.ipaam.transaction.application.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class BatchDepositTransferSuccessCommand {

    @TargetAggregateIdentifier
    String transactionId;
    String transactionCode;
    String transactionDate;
}