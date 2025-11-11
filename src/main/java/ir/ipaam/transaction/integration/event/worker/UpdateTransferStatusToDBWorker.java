package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.UpdateTransferStateCommand;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
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
            @Variable String transactionId
    ) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new RuntimeException("Transaction ID is required");
        }

        TransactionResponseStatus transactionResponseStatus  = TransactionResponseStatus.INPROGRESS;

        UpdateTransferStateCommand command = new UpdateTransferStateCommand(
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
