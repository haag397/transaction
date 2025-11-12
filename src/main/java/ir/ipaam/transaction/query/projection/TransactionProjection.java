package ir.ipaam.transaction.query.projection;

import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
import ir.ipaam.transaction.domain.event.PolTransferredEvent;
import ir.ipaam.transaction.domain.event.SatnaTransferredEvent;
import ir.ipaam.transaction.domain.event.TransactionStateUpdatedEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransactionType;
import ir.ipaam.transaction.query.model.Transaction;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
@ProcessingGroup("transaction-projections")
@RequiredArgsConstructor
@Slf4j
public class TransactionProjection {

    private final TransactionRepository transactionRepository;

    @EventHandler
    public void on(BatchDepositTransferedEvent event) {

        LocalDateTime when = parseIsoDate(event.getTransactionDate());

        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionId(event.getTransactionId())
                .transactionCode(event.getTransactionCode())
                .transactionDate(when)
                .amount(event.getAmount())
                .type(TransactionType.ACCOUNT_TRANSFER)
                .description(event.getDescription())
                .reason(event.getReason())
                .destinationTitle(event.getDestinationTitle())
                .status(event.getStatus()) // Save null for successful transfers, enum value for failures
                .build();

        transactionRepository.save(tx);
    }

    @EventHandler
    public void on(SatnaTransferredEvent event) {

        LocalDateTime when = parseIsoDate(event.getTransactionDate());
        String title = String.format("%s %s",
                event.getReceiverName() != null ? event.getReceiverName() : "",
                event.getReceiverLastName() != null ? event.getReceiverLastName() : "").trim();

        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionId(event.getTransactionId())
                .transactionCode(event.getTransactionCode())
                .transactionDate(when)
                .amount(event.getAmount())
                .destinationTitle(title.isEmpty() ? null : title)
                .type(TransactionType.SATNA)
                .status(Optional.ofNullable(event.getTransactionResponseStatus()).orElse(TransactionResponseStatus.INPROGRESS))
                .build();

        transactionRepository.save(tx);
    }

    @EventHandler
    public void on(PolTransferredEvent event) {

        // POL events currently do not carry ISO date/time; omit when for now
        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionId(event.getTransactionId())
                .amount(event.getAmount())
                .destination(event.getDestIban())
                .destinationTitle(event.getCreditorFullName())
                .type(TransactionType.POL)
                .status(Optional.ofNullable(event.getTransactionResponseStatus()).orElse(TransactionResponseStatus.INPROGRESS))
                .build();

        transactionRepository.save(tx);
    }

    @EventHandler
    public void on(TransactionStateUpdatedEvent event) {

        transactionRepository.findByTransactionId(event.getTransactionId())
                .ifPresent(tx -> {
                    tx.setStatus(event.getTransactionResponseStatus());
                    transactionRepository.save(tx);
                });
    }

    private LocalDateTime parseIsoDate(String iso) {
        if (iso == null) {
            return LocalDateTime.now();
        }
        try {
            return ZonedDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}



