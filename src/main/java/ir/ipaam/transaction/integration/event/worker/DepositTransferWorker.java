package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class DepositTransferWorker {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    @JobWorker(type = "deposit_transfer")
    public Map<String, Object> callDepositTransfer(
            @Variable CoreBatchDepositTransferRequestDTO coreBatchDepositTransferRequestDTO
    ) {
        CoreBatchDepositTransferResponseDTO response = coreService.batchDepositTransfer(coreBatchDepositTransferRequestDTO);
        commandGateway.sendAndWait(
                BatchDepositTransferedEvent.builder()
                        .transactionId(response.getTransactionId())
                        .transactionDate(response.getTransactionDate())
                        .transactionCode(response.getTransactionCode())
                        .transactionResponseStatus(TransactionResponseStatus.CALL_CORE).build()
        );
        return Map.of(
                "transactionId", response.getTransactionId(),
                "transactionDate", response.getTransactionDate(),
                "transactionCode", response.getTransactionCode()
        );
    }
}
