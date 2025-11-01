package ir.ipaam.transaction.application.command;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class UpdateDepositTransferStateCommand {
    @TargetAggregateIdentifier
    String transactionId;
    TransactionResponseStatus transactionResponseStatus;
}
