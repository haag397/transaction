package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import ir.ipaam.transaction.query.model.Transaction;
import ir.ipaam.transaction.query.repository.TransactionRepository;
import ir.ipaam.transaction.utills.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CommandGateway commandGateway;
    private final TransactionRepository repository;

    public String startBatchTransfer(CoreBatchDepositTransferRequestDTO request) {

        // Generate transactionId
        String transactionId = TransactionIdGenerator.generate();

        // Extract required fields (mapping from core DTO)
        CreditorDTO creditor = request.getCreditors().get(0);

        commandGateway.send(
                BatchDepositTransferCommand.builder()
                        .transactionId(transactionId)
                        .source(request.getSourceAccount())
                        .sourceTitle(request.getSourceComment())
                        .destination(creditor.getDestinationAccount())
                        .destinationTitle(creditor.getDestinationComment())
                        .amount(creditor.getDestinationAmount())
                        .description(request.getSourceComment())
                        .sourceDescription(request.getSourceComment())
                        .extraDescription(request.getSourceComment())
                        .extraInformation(Map.of()) // or actual map
                        .build()
        );

        return transactionId;
    }

    public Transaction getTransaction(String transactionId) {
        return repository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}
