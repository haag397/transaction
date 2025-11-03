package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BatchDepositTransferService {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    public BatchDepositTransferResponseDTO createBatchDepositTransfer(BatchDepositTransferRequestDTO request) {
        // Generate transaction ID
        String transactionId = UUID.randomUUID().toString();

        // Call core service first to process the transfer
        CoreBatchDepositTransferRequestDTO coreRequest = CoreBatchDepositTransferRequestDTO.builder()
                .transactionId(transactionId)
                .documentItemType(request.getDocumentItemType())
                .sourceAccount(request.getSourceAccount())
                .branchCode(request.getBranchCode())
                .sourceAmount(request.getSourceAmount())
                .sourceComment(request.getSourceComment())
                .transferBillNumber(request.getTransferBillNumber())
                .creditors(request.getCreditors())
                .build();

        CoreBatchDepositTransferResponseDTO coreResponse = coreService.batchDepositTransfer(coreRequest);

        if (coreResponse == null) {
            throw new RuntimeException("Failed to process batch deposit transfer");
        }

        // Create and send command to aggregate
        BatchDepositTransferCommand command = new BatchDepositTransferCommand(
                coreResponse.getTransactionId() != null ? coreResponse.getTransactionId() : transactionId,
                request.getDocumentItemType(),
                request.getSourceAccount(),
                request.getBranchCode(),
                request.getSourceAmount(),
                request.getSourceComment(),
                request.getTransferBillNumber(),
                request.getCreditors(),
                coreResponse.getTransactionCode(),
                coreResponse.getTransactionDate()
        );

        commandGateway.sendAndWait(command);

        return BatchDepositTransferResponseDTO.builder()
                .transactionId(coreResponse.getTransactionId())
                .transactionDate(coreResponse.getTransactionDate())
                .transactionCode(coreResponse.getTransactionCode())
                .status("SUCCESS")
                .message("Batch deposit transfer initiated successfully")
                .build();
    }
}

