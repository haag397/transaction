package ir.ipaam.transaction.domain.aggregate;

import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.application.command.TransactionInquiryCommand;
import ir.ipaam.transaction.application.command.UpdateDepositTransferStateCommand;
import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
import ir.ipaam.transaction.domain.event.TransactionStateUpdatedEvent;
import ir.ipaam.transaction.domain.event.TransactionInquiredEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.List;

@Aggregate
@NoArgsConstructor
public class BatchDepositTransferAggregate {
    
    @AggregateIdentifier
    private String transactionId;
    private String documentItemType;
    private String sourceAccount;
    private String branchCode;
    private Long sourceAmount;
    private String sourceComment;
    private String transferBillNumber;
    private List<CreditorDTO> creditors;
    private TransactionResponseStatus transactionStatus;
    private String transactionCode;
    private String transactionDate;

    @CommandHandler
    public BatchDepositTransferAggregate(BatchDepositTransferCommand command) {
        // Deposit-to-Deposit specific validation
        validateDepositTransfer(command);
        
        this.transactionId = command.getTransactionId();
        this.documentItemType = command.getDocumentItemType();
        this.sourceAccount = command.getSourceAccount();
        this.branchCode = command.getBranchCode();
        this.sourceAmount = command.getSourceAmount();
        this.sourceComment = command.getSourceComment();
        this.transferBillNumber = command.getTransferBillNumber();
        this.creditors = command.getCreditors();
        this.transactionStatus = TransactionResponseStatus.REQUESTED;

        // Emit event with core response data
        AggregateLifecycle.apply(BatchDepositTransferedEvent.builder()
                .transactionId(this.transactionId)
                .transactionCode(command.getTransactionCode())
                .transactionDate(command.getTransactionDate())
                .sourceAmount(this.sourceAmount)
                .transactionResponseStatus(TransactionResponseStatus.REQUESTED)
                .build());
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferedEvent event) {
        this.transactionId = event.getTransactionId();
        this.transactionCode = event.getTransactionCode();
        this.transactionDate = event.getTransactionDate();
        this.sourceAmount = event.getSourceAmount();
        if (event.getTransactionResponseStatus() != null) {
            this.transactionStatus = event.getTransactionResponseStatus();
        }
    }

    @CommandHandler
    public void handle(TransactionInquiryCommand command) {
        // Emit transaction inquiry event
        AggregateLifecycle.apply(new TransactionInquiredEvent(
                this.transactionDate,
                this.transactionCode,
                TransactionResponseStatus.TRANSACTION_INQUIRY
        ));
    }

    @EventSourcingHandler
    public void on(TransactionInquiredEvent event) {
        this.transactionStatus = event.getTransactionStatus();
        this.transactionCode = event.getTransactionCode();
        this.transactionDate = event.getTransactionDate();
    }

    @CommandHandler
    public void handle(UpdateDepositTransferStateCommand command) {
        // Emit state update event
        AggregateLifecycle.apply(new TransactionStateUpdatedEvent(
                command.getTransactionId(),
                command.getTransactionResponseStatus()
        ));
    }

    @EventSourcingHandler
    public void on(TransactionStateUpdatedEvent event) {
        this.transactionStatus = event.getTransactionResponseStatus();
    }

    // Deposit-to-Deposit specific business validation
    private void validateDepositTransfer(BatchDepositTransferCommand command) {
        if (command.getSourceAmount() == null || command.getSourceAmount() <= 0) {
            throw new IllegalArgumentException("Source amount must be positive");
        }
        if (command.getCreditors() == null || command.getCreditors().isEmpty()) {
            throw new IllegalArgumentException("Creditors list cannot be empty");
        }
        if (command.getSourceAccount() == null || command.getSourceAccount().isEmpty()) {
            throw new IllegalArgumentException("Source account is required");
        }
        // Add more Deposit-specific validation as needed
    }
}
