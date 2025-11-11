package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.PolTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CorePolTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CorePolTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;

import static ir.ipaam.transaction.utills.TransactionIdGenerator.generate;

@Component
@AllArgsConstructor
@Slf4j
public class PolTransferWorker {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    @JobWorker(type = "pol_transfer")
    public Map<String, Object> processPolTransfer(
            @Variable CorePolTransferRequestDTO polTransferRequest) {

        if (polTransferRequest == null) {
            throw new RuntimeException("POL transfer request is required");
        }

        // Generate transactionId if not provided
        String transactionId = polTransferRequest.getTransactionId();
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = generate();
            polTransferRequest.setTransactionId(transactionId);
        }

        log.info("Processing POL transfer for transactionId: {}", transactionId);

        // Call core service
        CorePolTransferResponseDTO coreResponse = coreService.polTransfer(polTransferRequest);

        if (coreResponse == null) {
            log.error("Core service returned null for transactionId: {}", transactionId);
            throw new RuntimeException("Failed to process POL transfer - core service returned null");
        }

        // Create and send command to POL aggregate
        PolTransferCommand command = new PolTransferCommand(
                transactionId,
                polTransferRequest.getIdentifier(),
                polTransferRequest.getIdentifierType(),
                polTransferRequest.getCustomerNumber(),
                polTransferRequest.getAmount(),
                polTransferRequest.getDestIban(),
                polTransferRequest.getTerminalType(),
                polTransferRequest.getDescription(),
                polTransferRequest.getPurposeCode(),
                polTransferRequest.getWithOutInquiry(),
                polTransferRequest.getCreditorFullName(),
                polTransferRequest.getPaymentId(),
                polTransferRequest.getEffectiveDate(),
                coreResponse.getReferenceNumber()
        );

        commandGateway.sendAndWait(command);

        log.info("POL transfer processed successfully for transactionId: {}", transactionId);

        return Map.of(
                "transactionId", transactionId,
                "referenceNumber", coreResponse.getReferenceNumber() != null ? coreResponse.getReferenceNumber() : "",
                "accepted", true
        );
    }
}

