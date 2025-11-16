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
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
@ProcessingGroup("transaction-command")
public class BatchDepositTransferAggregate {

    @AggregateIdentifier
    private String transactionId;

    private String source;
    private String sourceTitle;
    private String destination;
    private String destinationTitle;
    private Long amount;
    private String description;
    private String sourceDescription;
    private String extraDescription;
    private TransactionResponseStatus status;

    private boolean completed = false;

    @CommandHandler
    public BatchDepositTransferAggregate(BatchDepositTransferCommand cmd) {

        apply(BatchDepositTransferRequestedEvent.builder()
                .transactionId(cmd.getTransactionId())
                .source(cmd.getSource())
                .sourceTitle(cmd.getSourceTitle())
                .destination(cmd.getDestination())
                .destinationTitle(cmd.getDestinationTitle())
                .amount(cmd.getAmount())
                .description(cmd.getDescription())
                .sourceDescription(cmd.getSourceDescription())
                .extraDescription(cmd.getExtraDescription())
                .extraInformation(cmd.getExtraInformation())   // optional
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
        this.completed = false;
    }

    @CommandHandler
    public void handle(BatchDepositTransferSuccessCommand cmd) {

        if (completed) return;

        apply(new BatchDepositTransferSucceededEvent(
                cmd.getTransactionId(),
                cmd.getTransactionCode(),
                cmd.getTransactionDate()
        ));
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferSucceededEvent e) {
        this.completed = true;
    }

    @CommandHandler
    public void handle(BatchDepositTransferFailCommand cmd) {

        if (completed) return;

        apply(new BatchDepositTransferFailedEvent(
                cmd.getTransactionId(),
                cmd.getErrorMessage()
        ));
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferFailedEvent e) {
        this.completed = true;
    }

    @CommandHandler
    public void handle(BatchDepositTransferInquiredCommand cmd) {

        if (completed) return;

        apply(new BatchDepositTransferInquiredEvent(
                cmd.getTransactionId(),
                cmd.getTransactionCode(),
                cmd.getTransactionDate(),
                cmd.getStatus(),
                cmd.getRefNumber()
        ));
    }

    @EventSourcingHandler
    public void on(BatchDepositTransferInquiredEvent e) {
        this.completed = true;
    }
}