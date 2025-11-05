package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class SaveTransferRequestToDBWorker {

    private final CommandGateway commandGateway;
    private final CoreService coreService;
    private final TransactionRepository transactionRepository;

    @JobWorker(type = "save_to_db")
    public Map<String, Object> saveTransferRequestToDB(
            @Variable CoreBatchDepositTransferRequestDTO coreBatchDepositTransferRequestDTO) {

        // Generate transactionId if not provided
        String transactionId = coreBatchDepositTransferRequestDTO.getTransactionId();
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = java.util.UUID.randomUUID().toString();
        }

        BatchDepositTransferCommand command = new BatchDepositTransferCommand(
                        transactionId,
                        coreBatchDepositTransferRequestDTO.getDocumentItemType(),
                        coreBatchDepositTransferRequestDTO.getSourceAccount(),
                        coreBatchDepositTransferRequestDTO.getBranchCode(),
                        coreBatchDepositTransferRequestDTO.getSourceAmount(),
                        coreBatchDepositTransferRequestDTO.getSourceComment(),
                        coreBatchDepositTransferRequestDTO.getTransferBillNumber(),
                        coreBatchDepositTransferRequestDTO.getCreditors(),
                        null, // transactionCode - will be set after deposit_transfer
                        null  // transactionDate - will be set after deposit_transfer
                );

        commandGateway.sendAndWait(command);

        return Map.of(
                "transactionId", transactionId,
                "status", "REQUESTED"
        );
    }
}
