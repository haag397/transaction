package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateStreamCreationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static ir.ipaam.transaction.utills.TransactionIdGenerator.generate;

@Service
@RequiredArgsConstructor
public class BatchDepositTransferService {

    private final CommandGateway commandGateway;


    public CompletableFuture<BatchDepositTransferResponseDTO> createBatchDepositTransfer(BatchDepositTransferRequestDTO request) {
        // Generate transaction ID
        String transactionId = generate();

        // Prepare additional info map from request for the new command schema
        Map<String, Object> extraInformation = new HashMap<>();
        if (request.getDocumentItemType() != null) extraInformation.put("documentItemType", request.getDocumentItemType());
        if (request.getBranchCode() != null) extraInformation.put("branchCode", request.getBranchCode());
        if (request.getTransferBillNumber() != null) extraInformation.put("transferBillNumber", request.getTransferBillNumber());
        if (request.getCreditors() != null) extraInformation.put("creditors", request.getCreditors());

        // Map request into the new command model
        BatchDepositTransferCommand command = new BatchDepositTransferCommand(
                transactionId,                         // transactionId
                request.getSourceAccount(),            // source
                null,                                  // sourceTitle (not provided)
                null,                                  // destination (batch - not a single destination)
                null,                                  // destinationTitle
                request.getSourceAmount(),             // amount
                request.getSourceComment(),            // description
                null,                                  // sourceDescription
                null,                                  // extraDescription
                extraInformation,                      // extraInformation
                "BATCH_DEPOSIT_TRANSFER",              // reason
                null,                                  // transactionCode (set later by workflow/core)
                null                                   // transactionDate (set later by workflow/core)
        );

        try {
            commandGateway.sendAndWait(command);
        } catch (AggregateStreamCreationException duplicate) {
            // Idempotent create: aggregate already exists
        }

        // Immediate ACK; detailed fields will be filled as the workflow progresses
        BatchDepositTransferResponseDTO response = BatchDepositTransferResponseDTO.builder()
                .transactionId(transactionId)
                .status("REQUESTED")
                .message("Batch deposit transfer requested")
                .build();

        return CompletableFuture.completedFuture(response);
    }
}

