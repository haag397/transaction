package ir.ipaam.transaction.integration.event.handler;

import io.camunda.zeebe.client.ZeebeClient;
import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
import ir.ipaam.transaction.domain.event.SatnaTransferredEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ProcessingGroup("transaction-bpmn-integration")
@RequiredArgsConstructor
public class TransactionIntegrationEventHandler {

    private final ZeebeClient zeebe;

    @EventHandler
    public void on(BatchDepositTransferedEvent event) {
        try {
            zeebe.newCreateInstanceCommand()
                    .bpmnProcessId("Batch_Deposit_Transfer")
                    .latestVersion()
                    .variables(Map.of(
                            "transactionId", event.getTransactionId(),
                            "type", "deposit-to-deposit",
                            "transactionDate", event.getTransactionDate() != null ? event.getTransactionDate() : "",
                            "transactionCode", event.getTransactionCode() != null ? event.getTransactionCode() : "",
                            "amount", event.getAmount() != null ? event.getAmount() : 0L,
                            "accepted", false
                    ))
                    .send()
                    .join();
        } catch (Exception e) {
            throw new RuntimeException("Batch deposit transfer workflow failed", e);
        }
    }

    @EventHandler
    public void on(SatnaTransferredEvent event) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("transactionId", event.getTransactionId());
            variables.put("type", "satna");
            variables.put("transactionDate", event.getTransactionDate() != null ? event.getTransactionDate() : "");
            variables.put("transactionCode", event.getTransactionCode() != null ? event.getTransactionCode() : "");
            variables.put("amount", event.getAmount() != null ? event.getAmount() : 0L);
            variables.put("receiverName", event.getReceiverName() != null ? event.getReceiverName() : "");
            variables.put("receiverLastName", event.getReceiverLastName() != null ? event.getReceiverLastName() : "");
            variables.put("destinationDepNum", event.getDestinationDepNum() != null ? event.getDestinationDepNum() : "");
            variables.put("sourceDepNum", event.getSourceDepNum() != null ? event.getSourceDepNum() : "");
            variables.put("userReferenceNumber", event.getUserReferenceNumber() != null ? event.getUserReferenceNumber() : "");
            variables.put("accepted", false);
            
            zeebe.newCreateInstanceCommand()
                    .bpmnProcessId("Batch_Deposit_Transfer")
                    .latestVersion()
                    .variables(variables)
                    .send()
                    .join();
        } catch (Exception e) {
            throw new RuntimeException("SATNA transfer workflow failed", e);
        }
    }
}

