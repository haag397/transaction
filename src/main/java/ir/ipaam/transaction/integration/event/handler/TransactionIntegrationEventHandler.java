package ir.ipaam.transaction.integration.event.handler;

import io.camunda.zeebe.client.ZeebeClient;
import ir.ipaam.transaction.domain.event.BatchDepositTransferedEvent;
import ir.ipaam.transaction.domain.event.PolTransferredEvent;
import ir.ipaam.transaction.domain.event.SatnaTransferredEvent;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
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
            CoreBatchDepositTransferRequestDTO.CoreBatchDepositTransferRequestDTOBuilder builder = CoreBatchDepositTransferRequestDTO.builder()
                    .sourceAccount(event.getSource() != null ? event.getSource() : "")
                    .sourceAmount(event.getAmount() != null ? event.getAmount() : 0L)
                    .sourceComment(event.getDescription() != null ? event.getDescription() : "");

            // Extract fields from extraInformation map
            if (event.getExtraInformation() != null) {
                Map<String, Object> extraInfo = event.getExtraInformation();
                if (extraInfo.get("documentItemType") != null) {
                    builder.documentItemType(extraInfo.get("documentItemType").toString());
                }
                if (extraInfo.get("branchCode") != null) {
                    builder.branchCode(extraInfo.get("branchCode").toString());
                }
                if (extraInfo.get("transferBillNumber") != null) {
                    builder.transferBillNumber(extraInfo.get("transferBillNumber").toString());
                }
                if (extraInfo.get("creditors") != null) {
                    @SuppressWarnings("unchecked")
                    java.util.List<CreditorDTO> creditors = (java.util.List<CreditorDTO>) extraInfo.get("creditors");
                    builder.creditors(creditors);
                }
            }

            CoreBatchDepositTransferRequestDTO coreRequest = builder.build();

            // Extract transaction type from extraInformation (default to "deposit-to-deposit")
            String transactionType = "deposit-to-deposit";
            if (event.getExtraInformation() != null && event.getExtraInformation().get("type") != null) {
                transactionType = event.getExtraInformation().get("type").toString();
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("transactionId", event.getTransactionId());
            variables.put("coreBatchDepositTransferRequestDTO", coreRequest);
            variables.put("type", transactionType);
            variables.put("accepted", false);

            zeebe.newCreateInstanceCommand()
                    .bpmnProcessId("Batch_Deposit_Transfer")
                    .latestVersion()
                    .variables(variables)
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

    @EventHandler
    public void on(PolTransferredEvent event) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("transactionId", event.getTransactionId());
            variables.put("type", "pol");
            variables.put("accepted", false);
            // Add other POL-specific variables if needed by the workflow
            
            zeebe.newCreateInstanceCommand()
                    .bpmnProcessId("Batch_Deposit_Transfer")
                    .latestVersion()
                    .variables(variables)
                    .send()
                    .join();
        } catch (Exception e) {
            throw new RuntimeException("POL transfer workflow failed", e);
        }
    }
}

