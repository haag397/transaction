package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import ir.ipaam.transaction.utills.TransactionIdGenerator;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;

import static ir.ipaam.transaction.utills.TransactionIdGenerator.generate;

@Component
@AllArgsConstructor
public class SaveTransferRequestToDBWorker {

    private final CommandGateway commandGateway;
    private final CoreService coreService;
    private final TransactionRepository transactionRepository;

    @JobWorker(type = "save_to_db")
    public Map<String, Object> saveTransferRequestToDB(
            @Variable CoreBatchDepositTransferRequestDTO coreBatchDepositTransferRequestDTO,
            @Variable(name = "transactionId") String txIdVar) {

        // Resolve or generate transactionId safely
        String transactionId = txIdVar;
        if (coreBatchDepositTransferRequestDTO != null && coreBatchDepositTransferRequestDTO.getTransactionId() != null && !coreBatchDepositTransferRequestDTO.getTransactionId().isEmpty()) {
            transactionId = coreBatchDepositTransferRequestDTO.getTransactionId();
        }
        if (transactionId == null || transactionId.isEmpty()) transactionId = generate();

        // If DTO is missing, skip aggregate creation (task may be invoked from a generic flow)
        if (coreBatchDepositTransferRequestDTO != null) {
            BatchDepositTransferCommand command = new BatchDepositTransferCommand(
                            transactionId,
                            coreBatchDepositTransferRequestDTO.getDocumentItemType(),
                            coreBatchDepositTransferRequestDTO.getSourceAccount(),
                            coreBatchDepositTransferRequestDTO.getBranchCode(),
                            coreBatchDepositTransferRequestDTO.getSourceAmount(),
                            coreBatchDepositTransferRequestDTO.getSourceComment(),
                            coreBatchDepositTransferRequestDTO.getTransferBillNumber(),
                            coreBatchDepositTransferRequestDTO.getCreditors(),
                            null,
                            null
                    );

            try {
                commandGateway.sendAndWait(command);
            } catch (org.axonframework.modelling.command.AggregateStreamCreationException duplicate) {
                // Idempotent create: aggregate already exists
            }
        }

        return Map.of(
                "transactionId", transactionId,
                "status", "REQUESTED"
        );
    }
}
