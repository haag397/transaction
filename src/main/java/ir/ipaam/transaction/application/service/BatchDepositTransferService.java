package ir.ipaam.transaction.application.service;

import ir.ipaam.common.orchestration.QueryCommandFlowGateway;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateStreamCreationException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static ir.ipaam.transaction.utills.TransactionIdGenerator.generate;

@Service
@RequiredArgsConstructor
public class BatchDepositTransferService {

    private final CommandGateway commandGateway;
    private final CoreService coreService;
    private final QueryCommandFlowGateway flowGateway;


    public CompletableFuture<BatchDepositTransferResponseDTO> createBatchDepositTransfer(BatchDepositTransferRequestDTO request) {
        // Generate transaction ID
        String transactionId = generate();

        // Call core service first to process the transfer
//        CoreBatchDepositTransferRequestDTO coreRequest = CoreBatchDepositTransferRequestDTO.builder()
//                .transactionId(transactionId)
//                .documentItemType(request.getDocumentItemType())
//                .sourceAccount(request.getSourceAccount())
//                .branchCode(request.getBranchCode())
//                .sourceAmount(request.getSourceAmount())
//                .sourceComment(request.getSourceComment())
//                .transferBillNumber(request.getTransferBillNumber())
//                .creditors(request.getCreditors())
//                .build();
//
//        CoreBatchDepositTransferResponseDTO coreResponse = coreService.batchDepositTransfer(coreRequest);
//
//        if (coreResponse == null) {
//            throw new RuntimeException("Failed to process batch deposit transfer");
//        }

        // Create and send command to aggregate (always use our generated aggregate identifier)
//        BatchDepositTransferCommand command = new BatchDepositTransferCommand(
//                transactionId,
//                request.getDocumentItemType(),
//                request.getSourceAccount(),
//                request.getBranchCode(),
//                request.getSourceAmount(),
//                request.getSourceComment(),
//                request.getTransferBillNumber(),
//                request.getCreditors(),
//                coreResponse.getTransactionCode(),
//                coreResponse.getTransactionDate()
//        );

//        try {
//            commandGateway.sendAndWait(command);
//        } catch (AggregateStreamCreationException duplicate) {
            // Idempotent create: aggregate with this identifier already exists
        }

//        return BatchDepositTransferResponseDTO.builder()
//                .transactionId(coreResponse.getTransactionId())
//                .transactionDate(coreResponse.getTransactionDate())
//                .transactionCode(coreResponse.getTransactionCode())
//                .status("SUCCESS")
//                .message("Batch deposit transfer initiated successfully")
//                .build();
    }
}

