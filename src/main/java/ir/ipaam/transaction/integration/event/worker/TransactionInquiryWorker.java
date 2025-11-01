package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.domain.event.TransactionInquiredEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class TransactionInquiryWorker {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    @JobWorker(type = "transaction_inquiry")
    public Map<String, Object> transactionInquiry(
            @Variable CoreTransactionInquiryRequestDTO coreTransactionInquiryRequestDTO
    ){
        CoreTransactionInquiryResponseDTO response = coreService.transactionInquiry(coreTransactionInquiryRequestDTO);

        commandGateway.sendAndWait(new TransactionInquiredEvent(
                response.getTransactionDate(),
                response.getTransactionCode(),
                TransactionResponseStatus.REQUESTED
        ));
        return Map.of(
                "transactionDate", response.getTransactionStatus(),
                "transactionCode", response.getTransactionCode(),
                "status", response.getTransactionStatus()
        );
    }
}
