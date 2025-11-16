package ir.ipaam.transaction.query.projection;

import ir.ipaam.transaction.domain.event.*;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransactionType;
import ir.ipaam.transaction.query.model.Transaction;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ProcessingGroup("transaction-projection")
public class TransactionProjection {

    private final TransactionRepository repository;

    private LocalDateTime parseTransactionDate(String date) {

        if (date == null || date.isBlank()) {
            return null;
        }

        // Case 1: Try parsing ISO date (Inquiry API)
        try {
            return LocalDateTime.parse(date);
        } catch (Exception ignore) {}

        // Case 2: Persian date format (Transfer API: "1404/02/27 - 10:43:29")
        if (date.contains("/") && date.contains("-")) {
            // We cannot convert Shamsi → Gregorian here (no library)
            // So return null (or change your entity to String)
            return null;
        }

        // Unknown format
        return null;
    }

    @EventHandler
    public void on(BatchDepositTransferRequestedEvent e) {

        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
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
                .status(TransactionResponseStatus.INPROGRESS)
                .build();

        repository.save(tx);
    }

    @EventHandler
    public void on(BatchDepositTransferSucceededEvent e) {

        repository.findByTransactionId(e.getTransactionId()).ifPresent(tx -> {
            tx.setTransactionCode(e.getTransactionCode());
            tx.setTransactionDate(parseTransactionDate(e.getTransactionDate()));
//            tx.setTransactionDate(e.getTransactionDate());
            tx.setStatus(TransactionResponseStatus.SUCCESS);
            repository.save(tx);
        });
    }

    @EventHandler
    public void on(BatchDepositTransferFailedEvent e) {

        repository.findByTransactionId(e.getTransactionId()).ifPresent(tx -> {
            tx.setStatus(TransactionResponseStatus.UNSUCCESS);
            tx.setErrorMessage(e.getErrorMessage());
            repository.save(tx);
        });
    }

    @EventHandler
    public void on(BatchDepositTransferInquiredEvent e) {

        repository.findByTransactionId(e.getTransactionId()).ifPresent(tx -> {

            tx.setTransactionCode(e.getTransactionCode());
            tx.setTransactionDate(parseTransactionDate(e.getTransactionDate()));
            tx.setStatus(e.getStatus());
            tx.setRefNumber(e.getRefNumber());

            repository.save(tx);
        });
    }
}



