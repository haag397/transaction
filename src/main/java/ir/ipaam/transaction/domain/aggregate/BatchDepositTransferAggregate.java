package ir.ipaam.transaction.domain.aggregate;

import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferFailCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferInquiredCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferSuccessCommand;
import ir.ipaam.transaction.domain.event.BatchDepositTransferFailedEvent;
import ir.ipaam.transaction.domain.event.BatchDepositTransferInquiredEvent;
import ir.ipaam.transaction.domain.event.BatchDepositTransferRequestedEvent;
import ir.ipaam.transaction.domain.event.BatchDepositTransferSucceededEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Map;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
//@ProcessingGroup("transaction-command")
public class BatchDepositTransferAggregate {

    @AggregateIdentifier
    private String transactionId;
    String source;
    String sourceTitle;
    String destination;
    String destinationTitle;
    Long amount;
    String description;
    String sourceDescription;
    String extraDescription;
    Map<String, Object> extraInformation;
    String transactionCode;
    String transactionDate;
    TransactionResponseStatus status;
    String reason;
    String refNumber;
    TransactionType type;
    TransactionSubType subType;
    private boolean completed = false;

    @CommandHandler
    public BatchDepositTransferAggregate(BatchDepositTransferCommand cmd) {

        apply(BatchDepositTransferRequestedEvent.builder()
                .transactionId(cmd.getTransactionId())
                .id(cmd.getId())
                .source(cmd.getSource())
                .sourceTitle(cmd.getSourceTitle())
                .destination(cmd.getDestination())
                .destinationTitle(cmd.getDestinationTitle())
                .amount(cmd.getAmount())
                .description(cmd.getDescription())
                .sourceDescription(cmd.getSourceDescription())
                .extraDescription(cmd.getExtraDescription())
                .reason(cmd.getReason())
                .extraInformation(cmd.getExtraInformation())
                .type(cmd.getType())
                .subType(cmd.getSubType())
                .build()
        );
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferRequestedEvent e) {
        this.transactionId = e.getTransactionId();
        this.source = e.getSource();
        this.sourceTitle = e.getSourceTitle();
        this.destination = e.getDestination();
        this.destinationTitle = e.getDestinationTitle();
        this.amount = e.getAmount();
        this.description = e.getDescription();
        this.sourceDescription = e.getSourceDescription();
        this.extraDescription = e.getExtraDescription();
        this.extraInformation = e.getExtraInformation();
        this.reason = e.getReason();
        this.type = e.getType();
        this.subType = e.getSubType();
    }

    @CommandHandler
    public void handle(BatchDepositTransferSuccessCommand cmd) {
        if (!completed) {
            apply(new BatchDepositTransferSucceededEvent(
                    cmd.getTransactionId(),
                    cmd.getTransactionCode(),
                    cmd.getTransactionDate()
            ));
        }
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferSucceededEvent e) {
        this.completed = true;
        this.transactionId = e.getTransactionId();
        this.transactionCode = e.getTransactionCode();
        this.transactionDate = e.getTransactionDate();

    }

    @CommandHandler
    public void handle(BatchDepositTransferFailCommand cmd) {
        if (!completed) {
            apply(new BatchDepositTransferFailedEvent(
                    cmd.getTransactionId()
            ));
        }
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferFailedEvent e) {
        this.completed = true;
        this.transactionId = e.getTransactionId();
    }

    @CommandHandler
    public void handle(BatchDepositTransferInquiredCommand cmd) {
        if (!completed) {
            apply(new BatchDepositTransferInquiredEvent(
                    cmd.getTransactionId(),
                    cmd.getTransactionCode(),
                    cmd.getTransactionDate(),
                    cmd.getStatus(),
                    cmd.getRefNumber()
            ));
        }
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferInquiredEvent e) {
        this.completed = true;
        this.transactionId = e.getTransactionId();
        this.transactionCode = e.getTransactionCode();
        this.transactionDate = e.getTransactionDate();
        this.status = e.getStatus();
        this.refNumber = e.getRefNumber();
    }
}
