package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateStreamCreationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class SaveTransferRequestToDBWorker {

    private final CommandGateway commandGateway;

    @JobWorker(type = "save_to_db")
    public Map<String, Object> saveTransferRequestToDB(
            @Variable CoreBatchDepositTransferRequestDTO coreBatchDepositTransferRequestDTO,
            @Variable(name = "transactionId") String transactionId) {

        // Validate required parameters
        if (coreBatchDepositTransferRequestDTO == null) {
            throw new IllegalArgumentException("coreBatchDepositTransferRequestDTO cannot be null");
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("transactionId cannot be null or empty");
        }

        // Extract values from first creditor if available
        List<CreditorDTO> creditors = coreBatchDepositTransferRequestDTO.getCreditors();
        String destination = null;
        String destinationTitle = null;
        String extraDescription = null;

        if (creditors != null && !creditors.isEmpty()) {
            CreditorDTO firstCreditor = creditors.get(0);
            destination = firstCreditor.getDestinationAccount();
            destinationTitle = firstCreditor.getDestinationComment();
            
            // Aggregate destination comments from all creditors for extraDescription
            if (creditors.size() > 1) {
                extraDescription = creditors.stream()
                        .map(CreditorDTO::getDestinationComment)
                        .filter(comment -> comment != null && !comment.isEmpty())
                        .reduce((c1, c2) -> c1 + "; " + c2)
                        .orElse(null);
            } else {
                extraDescription = firstCreditor.getDestinationComment();
            }
        }

        // Prepare extraInformation map from DTO fields
        Map<String, Object> extraInformation = new HashMap<>();
        if (coreBatchDepositTransferRequestDTO.getDocumentItemType() != null) {
            extraInformation.put("documentItemType", coreBatchDepositTransferRequestDTO.getDocumentItemType());
        }
        if (coreBatchDepositTransferRequestDTO.getBranchCode() != null) {
            extraInformation.put("branchCode", coreBatchDepositTransferRequestDTO.getBranchCode());
        }
        if (coreBatchDepositTransferRequestDTO.getTransferBillNumber() != null) {
            extraInformation.put("transferBillNumber", coreBatchDepositTransferRequestDTO.getTransferBillNumber());
        }
        if (creditors != null) {
            extraInformation.put("creditors", creditors);
        }

        BatchDepositTransferCommand command = BatchDepositTransferCommand.builder()
                .transactionId(transactionId)
                .source(coreBatchDepositTransferRequestDTO.getSourceAccount())
                .destination(destination)
                .destinationTitle(destinationTitle)
                .amount(coreBatchDepositTransferRequestDTO.getSourceAmount())
                .description(coreBatchDepositTransferRequestDTO.getSourceComment())
                .extraDescription(extraDescription)
                .extraInformation(extraInformation)
                .build();

        try {
            commandGateway.sendAndWait(command);
        } catch (AggregateStreamCreationException duplicate) {
            // Idempotent create: aggregate already exists
        }

        return Map.of(
                "transactionId", transactionId,
                "status", "REQUESTED"
        );
    }
}
