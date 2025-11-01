package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import ir.ipaam.transaction.query.repository.BatchDepositTransferRepository;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class SaveTransferRequestToDBWorker {

    private final CommandGateway commandGateway;
    private final CoreService coreService;
    private final BatchDepositTransferRepository batchDepositTransferRepository;

    @JobWorker(type = "save_db")
    public Map<String, Object> batchDepositTransfer(
            @Variable CoreBatchDepositTransferRequestDTO coreBatchDepositTransferRequestDTO) {

        BatchDepositTransferCommand command =
                new BatchDepositTransferCommand(
                        coreBatchDepositTransferRequestDTO.getTransactionId(),
                        coreBatchDepositTransferRequestDTO.getDocumentItemType(),
                        coreBatchDepositTransferRequestDTO.getSourceAccount(),
                        coreBatchDepositTransferRequestDTO.getBranchCode(),
                        coreBatchDepositTransferRequestDTO.getSourceAmount(),
                        coreBatchDepositTransferRequestDTO.getSourceComment(),
                        coreBatchDepositTransferRequestDTO.getTransferBillNumber(),
                        coreBatchDepositTransferRequestDTO.getCreditors()
                );

        commandGateway.sendAndWait(command);

        return Map.of("status", "REQUESTED");
    }
}
