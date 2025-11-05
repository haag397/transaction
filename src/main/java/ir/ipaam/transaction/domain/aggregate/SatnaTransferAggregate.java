package ir.ipaam.transaction.domain.aggregate;

import ir.ipaam.transaction.application.command.SatnaTransferCommand;
import ir.ipaam.transaction.application.command.TransactionInquiryCommand;
import ir.ipaam.transaction.application.command.UpdateSatnaTransferStateCommand;
import ir.ipaam.transaction.domain.event.SatnaTransferredEvent;
import ir.ipaam.transaction.domain.event.TransactionInquiredEvent;
import ir.ipaam.transaction.domain.event.TransactionStateUpdatedEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class SatnaTransferAggregate {
    
    @AggregateIdentifier
    private String transactionId;
    private Long amount;
    private String destinationDepNum;
    private String receiverName;
    private String receiverLastName;
    private String sourceDepNum;
    private String description;
    private String detailType;
    private Boolean isAutoVerify;
    private String transactionBillNumber;
    private String senderReturnDepositNumber;
    private String senderCustomerNumber;
    private String commissionDepositNumber;
    private String destBankCode;
    private String senderPostalCode;
    private String senderNationalCode;
    private String senderShahabCode;
    private String senderNameOrCompanyType;
    private String senderFamilyNameOrCompanyName;
    private Boolean isFromBox;
    private TransactionResponseStatus transactionStatus;
    private String transactionCode;
    private String transactionDate;
    private String userReferenceNumber;

    @CommandHandler
    public SatnaTransferAggregate(SatnaTransferCommand command) {
        // SATNA-specific business validation
        validateSatnaTransfer(command);
        
        // Set state
        this.transactionId = command.getTransactionId();
        this.amount = command.getAmount();
        this.destinationDepNum = command.getDestinationDepNum();
        this.receiverName = command.getReceiverName();
        this.receiverLastName = command.getReceiverLastName();
        this.sourceDepNum = command.getSourceDepNum();
        this.description = command.getDescription();
        this.detailType = command.getDetailType();
        this.isAutoVerify = command.getIsAutoVerify();
        this.transactionBillNumber = command.getTransactionBillNumber();
        this.senderReturnDepositNumber = command.getSenderReturnDepositNumber();
        this.senderCustomerNumber = command.getSenderCustomerNumber();
        this.commissionDepositNumber = command.getCommissionDepositNumber();
        this.destBankCode = command.getDestBankCode();
        this.senderPostalCode = command.getSenderPostalCode();
        this.senderNationalCode = command.getSenderNationalCode();
        this.senderShahabCode = command.getSenderShahabCode();
        this.senderNameOrCompanyType = command.getSenderNameOrCompanyType();
        this.senderFamilyNameOrCompanyName = command.getSenderFamilyNameOrCompanyName();
        this.isFromBox = command.getIsFromBox();
        this.transactionStatus = TransactionResponseStatus.REQUESTED;

        // Emit SATNA-specific event
        AggregateLifecycle.apply(SatnaTransferredEvent.builder()
                .transactionId(this.transactionId)
                .transactionCode(command.getTransactionCode())
                .transactionDate(command.getTransactionDate())
                .amount(this.amount)
                .receiverName(this.receiverName)
                .receiverLastName(this.receiverLastName)
                .destinationDepNum(this.destinationDepNum)
                .sourceDepNum(this.sourceDepNum)
                .userReferenceNumber(command.getUserReferenceNumber())
                .transactionResponseStatus(TransactionResponseStatus.REQUESTED)
                .build());
    }

    @EventSourcingHandler
    public void on(SatnaTransferredEvent event) {
        this.transactionId = event.getTransactionId();
        this.transactionCode = event.getTransactionCode();
        this.transactionDate = event.getTransactionDate();
        this.amount = event.getAmount();
        this.receiverName = event.getReceiverName();
        this.receiverLastName = event.getReceiverLastName();
        this.destinationDepNum = event.getDestinationDepNum();
        this.sourceDepNum = event.getSourceDepNum();
        this.userReferenceNumber = event.getUserReferenceNumber();
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
    public void handle(UpdateSatnaTransferStateCommand command) {
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

    // SATNA-specific business validation
    private void validateSatnaTransfer(SatnaTransferCommand command) {
        if (command.getAmount() == null || command.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (command.getDestinationDepNum() == null || command.getDestinationDepNum().isEmpty()) {
            throw new IllegalArgumentException("Destination deposit number is required");
        }
        if (command.getReceiverName() == null || command.getReceiverName().isEmpty()) {
            throw new IllegalArgumentException("Receiver name is required");
        }
        // Add more SATNA-specific validation as needed
    }
}

