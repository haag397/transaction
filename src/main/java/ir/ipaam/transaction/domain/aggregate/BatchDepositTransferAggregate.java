package ir.ipaam.transaction.domain.aggregate;

import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.application.command.TransactionInquiryCommand;
import ir.ipaam.transaction.application.command.UpdateTransferStateCommand;
import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
import ir.ipaam.transaction.domain.event.TransactionInquiredEvent;
import ir.ipaam.transaction.domain.event.TransactionStateUpdatedEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Map;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
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
    private Map<String, Object> extraInformation;
    private String reason;
    private TransactionResponseStatus status;
    private TransactionType type;
    private TransactionSubType subType;
    private String transactionDate;
    private String transactionCode;

    @CommandHandler
    public BatchDepositTransferAggregate(BatchDepositTransferCommand command) {
        BatchDepositTransferedEvent event = BatchDepositTransferedEvent.builder()
                .transactionId(command.getTransactionId())
                .source(command.getSource())
                .sourceTitle(command.getSourceTitle())
                .destination(command.getDestination())
                .destinationTitle(command.getDestinationTitle())
                .amount(command.getAmount())
                .description(command.getDescription())
                .sourceDescription(command.getSourceDescription())
                .extraDescription(command.getExtraDescription())
                .extraInformation(command.getExtraInformation())
                .reason(command.getReason())
                .transactionCode(command.getTransactionCode())
                .transactionDate(command.getTransactionDate())
//                .status(TransactionResponseStatus.INPROGRESS)
                .build();

        apply(event);
    }


    @EventSourcingHandler
    public void on(BatchDepositTransferedEvent event) {
        this.transactionId = event.getTransactionId();
        this.source = event.getSource();
        this.sourceTitle = event.getSourceTitle();
        this.destination = event.getDestination();
        this.destinationTitle = event.getDestinationTitle();
        this.amount = event.getAmount();
        this.description = event.getDescription();
        this.sourceDescription = event.getSourceDescription();
        this.extraDescription = event.getExtraDescription();
        this.extraInformation = event.getExtraInformation();
        this.reason = event.getReason();
        this.transactionCode = event.getTransactionCode();
        this.transactionDate = event.getTransactionDate();
//        this.status = event.getStatus();

    }

    @CommandHandler
    public void handle(TransactionInquiryCommand command) {
        AggregateLifecycle.apply(new TransactionInquiredEvent(
                this.transactionDate,
                this.transactionCode,
                TransactionResponseStatus.INPROGRESS
        ));
    }

    @EventSourcingHandler
    public void on(TransactionInquiredEvent event) {
        this.status = event.getTransactionStatus();
        this.transactionCode = event.getTransactionCode();
        this.transactionDate = event.getTransactionDate();
    }

    @CommandHandler
    public void handle(UpdateTransferStateCommand command) {
        AggregateLifecycle.apply(new TransactionStateUpdatedEvent(
                command.getTransactionId(),
                command.getTransactionResponseStatus()
        ));
    }

    @EventSourcingHandler
    public void on(TransactionStateUpdatedEvent event) {
        this.status = event.getTransactionResponseStatus();
    }
}