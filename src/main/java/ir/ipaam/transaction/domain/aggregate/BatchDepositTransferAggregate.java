package ir.ipaam.transaction.domain.aggregate;

import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
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
                .build());
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferedEvent event) {
        this.transactionId = event.getTransactionId();
        this.transactionCode = event.getTransactionCode();
        this.transactionDate = event.getTransactionDate();
        if (event.getTransactionResponseStatus() != null) {
        }
    }
}
