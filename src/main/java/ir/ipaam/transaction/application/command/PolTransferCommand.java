package ir.ipaam.transaction.application.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class PolTransferCommand {
    @TargetAggregateIdentifier
    String transactionId;
    String identifier;
    Integer identifierType;
    String customerNumber;
    Long amount;
    String destIban;
    Integer terminalType;
    String description;
    Integer purposeCode;
    Boolean withOutInquiry;
    String creditorFullName;
    String paymentId;
    String effectiveDate;
    String referenceNumber;
}
