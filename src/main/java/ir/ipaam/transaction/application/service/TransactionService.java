package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
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

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CommandGateway commandGateway;
    private final TransactionRepository repository;
    private final CoreService coreService;


    public String startBatchTransfer(BatchDepositTransferRequestDTO request) {

        String transactionId = TransactionIdGenerator.generate();

        var sourceHolder = coreService.depositAccountHolders(request.getSource());
        String sourceName = extractFullName(sourceHolder);

        var destHolder = coreService.depositAccountHolders(request.getDestination());
        String destName = extractFullName(destHolder);

        commandGateway.send(
                BatchDepositTransferCommand.builder()
                        .transactionId(transactionId)
                        .id(UUID.randomUUID())
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
                                "transferBillNumber", request.getTransferBillNumber()
                        ))
                        .type(TransactionType.ACCOUNT_TRANSFER)
                        .subType(TransactionSubType.charge)
                        .build()
        );

        return transactionId;
    }
    private String extractFullName(CoreDepositAccountHoldersResponseDTO res) {
        var owner = res.getResult().getData().getDepositOwnerInfos().get(0);
        return owner.getFirstName() + " " + owner.getLastName();
    }

    public Transaction getTransaction(String transactionId) {
        return repository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}
