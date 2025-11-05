package ir.ipaam.transaction.integration.event.handler;

import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
import ir.ipaam.transaction.domain.event.TransactionStateUpdatedEvent;
import ir.ipaam.transaction.domain.event.SatnaTransferredEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransactionType;
import ir.ipaam.transaction.query.model.Transaction;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchDepositTransferEventHandler {

    private final TransactionRepository transactionRepository;

    // Handle Deposit-to-Deposit Event
    @EventHandler
    public void on(BatchDepositTransferedEvent event) {
        log.info("Handling BatchDepositTransferedEvent for transactionId: {}", event.getTransactionId());

        LocalDateTime transactionDateTime = parseTransactionDate(event.getTransactionDate());

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionId(event.getTransactionId())
                .transactionCode(event.getTransactionCode())
                .transactionDate(transactionDateTime)
                .amount(event.getSourceAmount())
                .transactionType(TransactionType.ACCOUNT_TRANSFER)
                .transactionResponseStatus(event.getTransactionResponseStatus() != null 
                        ? event.getTransactionResponseStatus() 
                        : TransactionResponseStatus.REQUESTED)
                .build();

        TransactionRepository.save(transaction);
        
        log.info("Deposit transfer saved to database with id: {}", transaction.getId());
    }

    // Handle SATNA Event
    @EventHandler
    public void on(SatnaTransferredEvent event) {
        log.info("Handling SatnaTransferredEvent for transactionId: {}", event.getTransactionId());

        LocalDateTime transactionDateTime = parseTransactionDate(event.getTransactionDate());

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionId(event.getTransactionId())
                .transactionCode(event.getTransactionCode())
                .transactionDate(transactionDateTime)
                .amount(event.getAmount())
                .recieverName(event.getReceiverName())
                .recieverLastName(event.getReceiverLastName())
                .transactionType(TransactionType.SATNA)
                .transactionResponseStatus(event.getTransactionResponseStatus() != null 
                        ? event.getTransactionResponseStatus() 
                        : TransactionResponseStatus.REQUESTED)
                .build();

        TransactionRepository.save(transaction);
        
        log.info("SATNA transfer saved to database with id: {}", transaction.getId());
    }

    // Handle Deposit State Update Event
    @EventHandler
    public void on(TransactionStateUpdatedEvent event) {
        log.info("Handling TransactionStateUpdatedEvent for transactionId: {}", event.getTransactionId());

        updateTransferStatus(event.getTransactionId(), event.getTransactionResponseStatus());
    }

    // Handle SATNA State Update Event
    @EventHandler
    public void on(SatnaTransferStateUpdatedEvent event) {
        log.info("Handling SatnaTransferStateUpdatedEvent for transactionId: {}", event.getTransactionId());

        updateTransferStatus(event.getTransactionId(), event.getTransactionResponseStatus());
    }

    // Helper method to update status
    private void updateTransferStatus(String transactionId, TransactionResponseStatus status) {
        TransactionRepository.findByTransactionId(transactionId)
                .ifPresentOrElse(
                        transferRequest -> {
                            transferRequest.setTransactionResponseStatus(status);
                            TransactionRepository.save(transferRequest);
                            log.info("Updated transfer status to: {} for transactionId: {}", 
                                    status, transactionId);
                        },
                        () -> log.warn("Transfer not found for transactionId: {}", transactionId)
                );
    }

    // Helper method to parse transaction date
    private LocalDateTime parseTransactionDate(String transactionDate) {
        if (transactionDate != null) {
            try {
                return ZonedDateTime.parse(transactionDate, 
                        DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime();
            } catch (Exception e) {
                log.warn("Failed to parse transaction date: {}", transactionDate);
            }
        }
        return LocalDateTime.now();
    }
}

