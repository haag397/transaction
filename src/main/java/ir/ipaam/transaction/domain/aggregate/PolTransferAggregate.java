package ir.ipaam.transaction.domain.aggregate;

import ir.ipaam.transaction.application.command.PolTransferCommand;
import ir.ipaam.transaction.application.command.UpdatePolTransferStateCommand;
import ir.ipaam.transaction.domain.event.PolTransferredEvent;
import ir.ipaam.transaction.domain.event.TransactionStateUpdatedEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.StringUtils;

@Aggregate
@NoArgsConstructor
public class PolTransferAggregate {

    @AggregateIdentifier
    private String transactionId;
    private TransactionResponseStatus transactionStatus;
    private String referenceNumber;

    private String identifier;
    private Integer identifierType;
    private String customerNumber;
    private Long amount;
    private String destIban;
    private Integer terminalType;
    private String description;
    private Integer purposeCode;
    private Boolean withOutInquiry;
    private String creditorFullName;
    private String paymentId;
    private String effectiveDate;

    @CommandHandler
    public PolTransferAggregate(PolTransferCommand command) {
        validatePonTransfer(command);

        this.transactionId = command.getTransactionId();
        this.identifier = command.getIdentifier();
        this.identifierType = command.getIdentifierType();
        this.customerNumber = command.getCustomerNumber();
        this.amount = command.getAmount();
        this.destIban = command.getDestIban();
        this.terminalType = command.getTerminalType();
        this.description = command.getDescription();
        this.purposeCode = command.getPurposeCode();
        this.withOutInquiry = command.getWithOutInquiry();
        this.creditorFullName = command.getCreditorFullName();
        this.paymentId = command.getPaymentId();
        this.effectiveDate = command.getEffectiveDate();
        this.referenceNumber = command.getReferenceNumber();
        this.transactionStatus = TransactionResponseStatus.REQUESTED;

        AggregateLifecycle.apply(PolTransferredEvent.builder()
                .transactionId(this.transactionId)
                .identifier(this.identifier)
                .identifierType(this.identifierType)
                .customerNumber(this.customerNumber)
                .amount(this.amount)
                .destIban(this.destIban)
                .terminalType(this.terminalType)
                .description(this.description)
                .purposeCode(this.purposeCode)
                .withOutInquiry(this.withOutInquiry)
                .creditorFullName(this.creditorFullName)
                .paymentId(this.paymentId)
                .effectiveDate(this.effectiveDate)
                .referenceNumber(this.referenceNumber)
                .transactionResponseStatus(TransactionResponseStatus.REQUESTED)
                .build());
    }

    @EventSourcingHandler
    public void on(PolTransferredEvent event) {
        this.transactionId = event.getTransactionId();
        this.referenceNumber = event.getReferenceNumber();
        this.transactionStatus = event.getTransactionResponseStatus();
    }

    @CommandHandler
    public void handle(UpdatePolTransferStateCommand command) {
        AggregateLifecycle.apply(new TransactionStateUpdatedEvent(
                command.getTransactionId(),
                command.getTransactionResponseStatus()
        ));
    }

    @EventSourcingHandler
    public void on(TransactionStateUpdatedEvent event) {
        this.transactionStatus = event.getTransactionResponseStatus();
    }

    private void validatePonTransfer(PolTransferCommand command) {
        if (!StringUtils.hasText(command.getIdentifier())) {
            throw new IllegalArgumentException("Identifier is required");
        }
        if (!StringUtils.hasText(command.getCustomerNumber())) {
            throw new IllegalArgumentException("Customer number is required");
        }
        if (!StringUtils.hasText(command.getDestIban())) {
            throw new IllegalArgumentException("Destination IBAN is required");
        }
        if (command.getAmount() == null || command.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!StringUtils.hasText(command.getCreditorFullName())) {
            throw new IllegalArgumentException("Creditor full name is required");
        }
    }
}
