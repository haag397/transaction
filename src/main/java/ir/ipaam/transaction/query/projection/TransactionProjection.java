package ir.ipaam.transaction.query.projection;

import com.github.mfathi91.time.PersianDate;
import ir.ipaam.transaction.application.service.GetTransactionStatusQuery;
import ir.ipaam.transaction.domain.event.*;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.query.model.Transaction;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ProcessingGroup("transaction")
public class TransactionProjection {

    private final TransactionRepository repository;
    private final QueryUpdateEmitter queryUpdateEmitter;

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
                .reason(e.getReason())
                .status(TransactionResponseStatus.INPROGRESS)
                .build();

        repository.save(tx);
        queryUpdateEmitter.emit(
                GetTransactionStatusQuery.class,
                q -> q.getTransactionId().equals(e.getTransactionId()),
                tx
        );
    }

    @EventHandler
    public void on(BatchDepositTransferSucceededEvent e) {

        repository.findByTransactionId(e.getTransactionId()).ifPresent(tx -> {
            tx.setTransactionCode(e.getTransactionCode());
            tx.setTransactionDate(parse(e.getTransactionDate()));
            tx.setStatus(TransactionResponseStatus.SUCCESS);
            repository.save(tx);
            queryUpdateEmitter.emit(
                    GetTransactionStatusQuery.class,
                    q -> q.getTransactionId().equals(e.getTransactionId()),
                    tx
            );
        });
    }

    @EventHandler
    public void on(BatchDepositTransferFailedEvent e) {

        repository.findByTransactionId(e.getTransactionId()).ifPresent(tx -> {
            tx.setStatus(TransactionResponseStatus.UNSUCCESS);
            repository.save(tx);
            queryUpdateEmitter.emit(
                    GetTransactionStatusQuery.class,
                    q -> q.getTransactionId().equals(e.getTransactionId()),
                    tx
            );
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
            queryUpdateEmitter.emit(
                    GetTransactionStatusQuery.class,
                    q -> q.getTransactionId().equals(e.getTransactionId()),
                    tx
            );
        });
    }
    @QueryHandler
    public Transaction handle(GetTransactionStatusQuery query) {
        return repository.findByTransactionId(query.getTransactionId())
                .orElse(null);
    }

    private LocalDateTime parse(String input) {
        try {
            // input = "1404/02/27 - 10:43:29"
            String[] parts = input.split(" - ");
            String jalali = parts[0]; // 1404/02/27
            String time   = parts.length > 1 ? parts[1] : "00:00:00";

            // Parse Persian date manually
            String[] dateParts = jalali.split("/");
            int year  = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day   = Integer.parseInt(dateParts[2]);

            PersianDate p = PersianDate.of(year, month, day);
            LocalDate gregorian = p.toGregorian();

            LocalTime localTime = LocalTime.parse(time);

            return LocalDateTime.of(gregorian, localTime);

        } catch (Exception ex) {
            return null;
        }
    }


}
