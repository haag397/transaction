package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.application.command.BatchDepositTransferFailCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferInquiredCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferSuccessCommand;
import ir.ipaam.transaction.domain.event.BatchDepositTransferRequestedEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchDepositTransferOrchestrator {

    private final CoreService coreService;
    private final CommandGateway commandGateway;

    @EventHandler
    public void on(BatchDepositTransferRequestedEvent e) {

        try {
            // 1) Call Core batch transfer
            CoreBatchDepositTransferResponseDTO res =
                    coreService.batchDepositTransfer(buildCoreRequest(e));

            // 2) Success → send command
            commandGateway.send(new BatchDepositTransferSuccessCommand(
                    e.getTransactionId(),
                    res.getResult().getData().getTransactionCode(),
                    res.getResult().getData().getTransactionDate()
            ));

        } catch (Exception ex) {

            // 3) Fail → send fail command
            commandGateway.send(new BatchDepositTransferFailCommand(
                    e.getTransactionId(),
                    ex.getMessage()
            ));

            // 4) Inquiry after failure
            CoreTransactionInquiryResponseDTO inquiry =
                    coreService.transactionInquiry(e.getTransactionId());

            // 5) Send inquiry result
            commandGateway.send(new BatchDepositTransferInquiredCommand(
                    e.getTransactionId(),
                    inquiry.getResult().getData().getTransactionCode(),
                    inquiry.getResult().getData().getTransactionDate(),
                    mapStatus(inquiry.getResult().getData().getTransactionStatus()),
                    inquiry.getResult().getData().getTransactionStatus()
            ));
        }
    }

    private CoreBatchDepositTransferRequestDTO buildCoreRequest(BatchDepositTransferRequestedEvent e) {

        // YOU MUST HAVE THIS DTO ALREADY
        CoreBatchDepositTransferRequestDTO dto = new CoreBatchDepositTransferRequestDTO();
        dto.setSourceAccount(e.getSource());
        dto.setSourceComment(e.getSourceDescription());
        dto.setSourceAmount(e.getAmount());
        dto.setDocumentItemType("Deposit"); // or dynamic
        dto.setTransactionId(e.getTransactionId());

        // If you stored creditors info in extraInformation JSONB
        List<CreditorDTO> creditors =
                (List<CreditorDTO>) e.getExtraInformation().get("creditors");

        dto.setCreditors(creditors);

        return dto;
    }

    // Convert string status → enum
    private TransactionResponseStatus mapStatus(String status) {

        if (status == null) return TransactionResponseStatus.INPROGRESS;

        switch (status.toUpperCase()) {
            case "SUCCESS":
                return TransactionResponseStatus.SUCCESS;
            case "FAILED":
            case "UNSUCCESS":
                return TransactionResponseStatus.UNSUCCESS;
            case "INPROGRESS":
            case "PENDING":
                return TransactionResponseStatus.INPROGRESS;
            case "REVERSED":
                return TransactionResponseStatus.REVERSED;
        }

        // default
        return TransactionResponseStatus.INPROGRESS;
    }
}

