package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreDepositAccountHoldersResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import ir.ipaam.transaction.query.model.Transaction;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import ir.ipaam.transaction.utills.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CommandGateway commandGateway;
    private final TransactionRepository repository;
    private final CoreService coreService;

//    public CompletableFuture<CoreBatchDepositTransferResponseDTO> startBatchTransfer(
    public CompletableFuture<BatchDepositTransferResponseDTO> startBatchTransfer(

                    BatchDepositTransferRequestDTO request) {
        String transactionId = TransactionIdGenerator.generate();
        UUID id = UUID.randomUUID();

        var sourceHolder = coreService.depositAccountHolders(request.getSource());
        String sourceName = extractFullName(sourceHolder);

        var destHolder = coreService.depositAccountHolders(request.getDestination());
        String destName = extractFullName(destHolder);

        BatchDepositTransferCommand command = BatchDepositTransferCommand.builder()
                        .transactionId(transactionId)
                        .id(id)
                        .source(request.getSource())
                        .sourceTitle(sourceName)
                        .destination(request.getDestination())
                        .destinationTitle(destName)
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .sourceDescription(request.getSourceDescription())
                        .extraDescription(request.getDescription())
                        .reason(request.getReason())
                        .extraInformation(Map.of(
                                "description", request.getDescription()
                        ))
                        .type(TransactionType.ACCOUNT_TRANSFER)
                        .subType(TransactionSubType.charge)
                        .build();

        return commandGateway.send(command);
    }
    private String extractFullName(CoreDepositAccountHoldersResponseDTO res) {

//        if (res == null ||
//                res.getResult() == null ||
//                res.getResult().getData() == null ||
//                res.getResult().getData().getDepositOwnerInfos() == null ||
//                res.getResult().getData().getDepositOwnerInfos().isEmpty()) {
//            return "";
//        }

        return res.getResult()
                .getData()
                .getDepositOwnerInfos()
                .stream()
                .map(o -> o.getFirstName() + " " + o.getLastName())
                .collect(Collectors.joining(", "));
    }

    public Transaction getTransaction(String transactionId) {
        return repository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}
