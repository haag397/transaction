package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.UpdateDepositTransferStateCommand;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class UpdateTransferStatusToDBWorker {
    
    private final CommandGateway commandGateway;

    @JobWorker(type = "update_db")
    public Map<String, Object> updateDepositTransferStatus(
            @Variable String transactionId,
            @Variable CoreTransactionInquiryResponseDTO inquiryResponse
    ) {
        if (transactionId == null || inquiryResponse == null) {
            throw new RuntimeException("Transaction ID and inquiry response are required");
        }

        TransactionResponseStatus transactionResponseStatus  = TransactionResponseStatus.UPDATED;

        UpdateDepositTransferStateCommand command = new UpdateDepositTransferStateCommand(
                transactionId,
                transactionResponseStatus
        );
        
        commandGateway.sendAndWait(command);
        
        return Map.of(
                "transactionId", transactionId,
                "transactionResponseStatus", transactionResponseStatus
        );
    }
}
