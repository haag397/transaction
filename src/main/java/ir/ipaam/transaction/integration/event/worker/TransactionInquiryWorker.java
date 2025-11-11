package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.UpdateTransferStateCommand;
import ir.ipaam.transaction.domain.event.TransactionInquiredEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class TransactionInquiryWorker {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    @JobWorker(type = "transaction_inquiry")
    public Map<String, Object> transactionInquiry(@Variable String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new RuntimeException("Transaction ID is required for inquiry");
        }

        log.info("Processing transaction inquiry for transactionId: {}", transactionId);

        CoreTransactionInquiryResponseDTO response = coreService.transactionInquiry(transactionId);

        if (response == null) {
            log.error("Inquiry response is null for transactionId: {}", transactionId);
            // If inquiry fails, set status to UNSUCCESS
            updateTransactionStatus(transactionId, TransactionResponseStatus.UNSUCCESS);
            throw new RuntimeException("Failed to get transaction inquiry - response is null");
        }

        // Map inquiry response status string to TransactionResponseStatus enum
        TransactionResponseStatus status = mapInquiryStatusToTransactionStatus(response.getTransactionStatus());
        
        log.info("Inquiry result for transactionId: {} - status: {}", transactionId, status);

        // Update transaction status based on inquiry response
        updateTransactionStatus(transactionId, status);

        // Also send event for event sourcing
        commandGateway.sendAndWait(new TransactionInquiredEvent(
                response.getTransactionDate(),
                response.getTransactionCode(),
                status
        ));

        return Map.of(
                "transactionId", transactionId,
                "transactionDate", response.getTransactionDate() != null ? response.getTransactionDate() : "",
                "transactionCode", response.getTransactionCode() != null ? response.getTransactionCode() : "",
                "status", response.getTransactionStatus() != null ? response.getTransactionStatus() : ""
        );
    }

    private TransactionResponseStatus mapInquiryStatusToTransactionStatus(String inquiryStatus) {
        if (inquiryStatus == null || inquiryStatus.isBlank()) {
            return TransactionResponseStatus.UNSUCCESS;
        }

        String statusUpper = inquiryStatus.toUpperCase().trim();
        
        // Map common status strings to enum values
        switch (statusUpper) {
            case "SUCCESS":
            case "SUCCEEDED":
            case "COMPLETED":
                return TransactionResponseStatus.SUCCESS;
            case "UNSUCCESS":
            case "UNSUCCESSFUL":
            case "FAILED":
            case "FAILURE":
            case "ERROR":
                return TransactionResponseStatus.UNSUCCESS;
            case "REVERSED":
                return TransactionResponseStatus.REVERSED;
            case "REVERSING":
                return TransactionResponseStatus.REVERSING;
            case "INPROGRESS":
            case "IN_PROGRESS":
            case "PENDING":
            case "PROCESSING":
                return TransactionResponseStatus.INPROGRESS;
            default:
                log.warn("Unknown inquiry status: {}, defaulting to UNSUCCESS", inquiryStatus);
                return TransactionResponseStatus.UNSUCCESS;
        }
    }

    private void updateTransactionStatus(String transactionId, TransactionResponseStatus status) {
        try {
            log.info("Updating transaction status to {} for transactionId: {}", status, transactionId);
            commandGateway.sendAndWait(new UpdateTransferStateCommand(
                    transactionId,
                    status
            ));
        } catch (Exception ex) {
            log.error("Failed to update transaction status for transactionId: {}", transactionId, ex);
            // Don't throw - inquiry should still return the response even if status update fails
        }
    }
}
