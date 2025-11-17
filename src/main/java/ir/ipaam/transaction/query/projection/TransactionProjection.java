package ir.ipaam.transaction.query.projection;

import ir.ipaam.transaction.domain.event.*;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.query.model.Transaction;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@ProcessingGroup("transaction")
public class TransactionProjection {

    private final TransactionRepository repository;

    @EventHandler
    public void on(BatchDepositTransferRequestedEvent e) {

        Transaction tx = Transaction.builder()
                .id(e.getId())
                .transactionId(e.getTransactionId())
                .source(e.getSource())
                .sourceTitle(e.getSourceTitle())
                .destination(e.getDestination())
                .destinationTitle(e.getDestinationTitle())
                .amount(e.getAmount())
                .description(e.getDescription())
                .sourceDescription(e.getSourceDescription())
                .extraDescription(e.getExtraDescription())
                .extraInformation(e.getExtraInformation())
                .type(e.getType())
                .subType(e.getSubType())
                .status(TransactionResponseStatus.INPROGRESS)
                .build();

        repository.save(tx);
    }

    @EventHandler
    public void on(BatchDepositTransferSucceededEvent e) {

        repository.findByTransactionId(e.getTransactionId()).ifPresent(tx -> {
            tx.setTransactionCode(e.getTransactionCode());
            tx.setTransactionDate(parse(e.getTransactionDate()));
            tx.setStatus(TransactionResponseStatus.SUCCESS);
            repository.save(tx);
        });
    }

    @EventHandler
    public void on(BatchDepositTransferFailedEvent e) {

        repository.findByTransactionId(e.getTransactionId()).ifPresent(tx -> {
            tx.setStatus(TransactionResponseStatus.UNSUCCESS);
            repository.save(tx);
        });
    }

    @EventHandler
    public void on(BatchDepositTransferInquiredEvent e) {

        repository.findByTransactionId(e.getTransactionId()).ifPresent(tx -> {
            tx.setTransactionCode(e.getTransactionCode());
            tx.setTransactionDate(parse(e.getTransactionDate()));
            tx.setStatus(e.getStatus());
            tx.setRefNumber(e.getRefNumber());
            repository.save(tx);
        });
    }

    private LocalDateTime parse(String date) {
        if (date == null) return null;

        try { return LocalDateTime.parse(date); }
        catch (Exception ignore) {}

        return null;
    }
}
