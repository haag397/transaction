package ir.ipaam.transaction.application.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class TransactionInquiryCommand {
    @TargetAggregateIdentifier
    String id;
}
