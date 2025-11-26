package ir.ipaam.transaction.application.service;

import ir.ipaam.common.dto.QuerySubscriptionDTO;
import ir.ipaam.common.orchestration.QueryCommandFlowGateway;
import ir.ipaam.transaction.api.read.dto.GetTransactionStatusQuery;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
import ir.ipaam.transaction.integration.client.core.dto.CoreDepositAccountHoldersResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import ir.ipaam.transaction.query.model.Transaction;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import ir.ipaam.transaction.utills.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final CoreService coreService;
    private final QueryCommandFlowGateway queryCommandFlowGateway;

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

        return queryCommandFlowGateway.sendCommandAndWaitForAll(
                command,
                List.of(
                        new QuerySubscriptionDTO<>(
                                new GetTransactionStatusQuery(transactionId),
                                BatchDepositTransferResponseDTO.class
                        )
                ),
                results -> (BatchDepositTransferResponseDTO)
                        results.get(BatchDepositTransferResponseDTO.class),
                errors -> mapError(errors),
                Duration.ofSeconds(25)
        );
    }

    private String extractFullName(CoreDepositAccountHoldersResponseDTO res) {
        return res.getResult()
                .getData()
                .getDepositOwnerInfos()
                .stream()
                .map(o -> o.getFirstName() + " " + o.getLastName())
                .collect(Collectors.joining(", "));
    }

    public Transaction getTransaction(String transactionId) {
        return repository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("تراکنش پیدا نشد"));
    }

    private BatchDepositTransferResponseDTO mapError(Map<Class<?>, Object> errors) {

        BatchDepositTransferResponseDTO.Status status =
                BatchDepositTransferResponseDTO.Status.builder()
                        .code("500")
                        .message("FAILED")
                        .description("خطا در ارتباط با سرویس")
                        .build();

        return BatchDepositTransferResponseDTO.builder()
                .status(status)
                .meta(null)
                .result(null)
                .build();
    }
}
