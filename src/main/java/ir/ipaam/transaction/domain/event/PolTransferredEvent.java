package ir.ipaam.transaction.domain.event;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PolTransferredEvent {
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
    TransactionResponseStatus transactionResponseStatus;
}
