package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.SatnaTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreSatnaTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreSatnaTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class SatnaTransferWorker {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    @JobWorker(type = "satna_transfer")
    public Map<String, Object> processSatnaTransfer(
            @Variable CoreSatnaTransferRequestDTO satnaTransferRequest) {

        log.info("Processing SATNA transfer for transactionId: {}", satnaTransferRequest.getTransactionId());

        // Generate transactionId if not provided
        String transactionId = satnaTransferRequest.getTransactionId();
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
            satnaTransferRequest.setTransactionId(transactionId);
        }

        // Call core service
        CoreSatnaTransferResponseDTO coreResponse = coreService.satnaTransfer(satnaTransferRequest);

        if (coreResponse == null) {
            throw new RuntimeException("Failed to process SATNA transfer - core service returned null");
        }

        // Create and send command to SATNA aggregate
        SatnaTransferCommand command = new SatnaTransferCommand(
                coreResponse.getTransactionId() != null ? coreResponse.getTransactionId() : transactionId,
                satnaTransferRequest.getAmount(),
                satnaTransferRequest.getDestinationDepNum(),
                satnaTransferRequest.getReceiverName(),
                satnaTransferRequest.getReceiverLastName(),
                satnaTransferRequest.getSourceDepNum(),
                satnaTransferRequest.getDescription(),
                satnaTransferRequest.getDetailType(),
                satnaTransferRequest.getIsAutoVerify(),
                satnaTransferRequest.getTransactionBillNumber(),
                satnaTransferRequest.getSenderReturnDepositNumber(),
                satnaTransferRequest.getSenderCustomerNumber(),
                satnaTransferRequest.getCommissionDepositNumber(),
                satnaTransferRequest.getDestBankCode(),
                satnaTransferRequest.getSenderPostalCode(),
                satnaTransferRequest.getSenderNationalCode(),
                satnaTransferRequest.getSenderShahabCode(),
                satnaTransferRequest.getSenderNameOrCompanyType(),
                satnaTransferRequest.getSenderFamilyNameOrCompanyName(),
                satnaTransferRequest.getIsFromBox(),
                coreResponse.getTransactionCode(),
                coreResponse.getTransactionDate(),
                coreResponse.getUserReferenceNumber()
        );

        commandGateway.sendAndWait(command);

        log.info("SATNA transfer processed successfully for transactionId: {}", coreResponse.getTransactionId());

        return Map.of(
                "transactionId", coreResponse.getTransactionId(),
                "transactionCode", coreResponse.getTransactionCode(),
                "transactionDate", coreResponse.getTransactionDate(),
                "userReferenceNumber", coreResponse.getUserReferenceNumber(),
                "status", "SUCCESS"
        );
    }
}


