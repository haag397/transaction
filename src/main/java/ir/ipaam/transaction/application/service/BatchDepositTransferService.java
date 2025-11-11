package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateStreamCreationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static ir.ipaam.transaction.utills.TransactionIdGenerator.generate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchDepositTransferService {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    public CompletableFuture<BatchDepositTransferResponseDTO> createBatchDepositTransfer(BatchDepositTransferRequestDTO request) {
        String transactionId = generate();

        CoreBatchDepositTransferRequestDTO coreRequest = CoreBatchDepositTransferRequestDTO.builder()
                .transactionId(transactionId)
                .documentItemType(request.getDocumentItemType())
                .sourceAccount(request.getSourceAccount())
                .sourceAmount(request.getSourceAmount())
                .sourceComment(request.getSourceComment())
                .creditors(request.getCreditors())
                .build();

        CoreBatchDepositTransferResponseDTO coreResponse = coreService.batchDepositTransfer(coreRequest);

        if (coreResponse == null) {
            throw new RuntimeException("Failed to process batch deposit transfer - core service returned null");
        }

        Map<String, Object> extraInformation = new HashMap<>();
        if (request.getDocumentItemType() != null) extraInformation.put("documentItemType", request.getDocumentItemType());
        if (request.getBranchCode() != null) extraInformation.put("branchCode", request.getBranchCode());
        if (request.getCreditors() != null) extraInformation.put("creditors", request.getCreditors());

        // Map request into the new command model
        BatchDepositTransferCommand command = new BatchDepositTransferCommand(
                transactionId,                    // transactionId (use from core response if available)
                request.getSourceAccount(),            // source
                null,                                  // sourceTitle (not provided)
                null,                                  // destination (batch - not a single destination)
                null,                                  // destinationTitle
                request.getSourceAmount(),             // amount
                request.getSourceComment(),            // description
                null,                                  // sourceDescription
                null,                                  // extraDescription
                extraInformation,                      // extraInformation
                null,                                  // reason
                coreResponse.getResult().getData().getTransactionId(),               // transactionCode (from core response)
                coreResponse.getResult().getData().getTransactionDate()                // transactionDate (from core response)
        );

        commandGateway.sendAndWait(command);

        // Map core response to API response DTO
        BatchDepositTransferResponseDTO response = BatchDepositTransferResponseDTO.builder()
                .transactionId(transactionId)
                .transactionDate(coreResponse.getResult().getData().getTransactionDate())
                .transactionCode(coreResponse.getResult().getData().getTransactionId())
                .build();

        return CompletableFuture.completedFuture(response);
    }
}

