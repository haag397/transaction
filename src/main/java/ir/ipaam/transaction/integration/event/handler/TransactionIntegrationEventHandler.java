package ir.ipaam.transaction.integration.event.handler;

import io.camunda.zeebe.client.ZeebeClient;
import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
import ir.ipaam.transaction.domain.event.SatnaTransferredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ProcessingGroup("transaction-bpmn-integration")
@RequiredArgsConstructor
@Slf4j
public class TransactionIntegrationEventHandler {

    private final ZeebeClient zeebe;

    @EventHandler
    public void on(BatchDepositTransferedEvent event) {
        try {
            log.info("Starting BPMN process for BatchDepositTransferedEvent with transactionId: {}", event.getTransactionId());
            
            zeebe.newCreateInstanceCommand()
                    .bpmnProcessId("Batch_Deposit_Transfer")
                    .latestVersion()
                    .variables(Map.of(
                            "transactionId", event.getTransactionId(),
                            "type", "deposit-to-deposit",
                            "transactionDate", event.getTransactionDate() != null ? event.getTransactionDate() : "",
                            "transactionCode", event.getTransactionCode() != null ? event.getTransactionCode() : "",
                            "amount", event.getSourceAmount() != null ? event.getSourceAmount() : 0L,
                            "accepted", false
                    ))
                    .send()
                    .join();
            
            log.info("BPMN process started successfully for BatchDepositTransfer with transactionId: {}", event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to start BPMN process for BatchDepositTransfer with transactionId: {}", event.getTransactionId(), e);
            throw new RuntimeException("Batch deposit transfer workflow failed", e);
        }
    }

    @EventHandler
    public void on(SatnaTransferredEvent event) {
        try {
            log.info("Starting BPMN process for SatnaTransferredEvent with transactionId: {}", event.getTransactionId());
            
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
            
            log.info("BPMN process started successfully for SatnaTransfer with transactionId: {}", event.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to start BPMN process for SatnaTransfer with transactionId: {}", event.getTransactionId(), e);
            throw new RuntimeException("SATNA transfer workflow failed", e);
        }
    }
}

