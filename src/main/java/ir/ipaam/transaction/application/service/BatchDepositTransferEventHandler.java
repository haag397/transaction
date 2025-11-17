package ir.ipaam.transaction.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import ir.ipaam.transaction.application.command.BatchDepositTransferFailCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferInquiredCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferSuccessCommand;
import ir.ipaam.transaction.domain.event.BatchDepositTransferRequestedEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.integration.client.core.dto.*;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ProcessingGroup("transaction")
public class BatchDepositTransferEventHandler {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    @EventHandler
    public void on(BatchDepositTransferRequestedEvent event) {

        log.info("Handler START → {}", event.getTransactionId());

        try {
            String sourceName = extractFullName(coreService.depositAccountHolders(event.getSource()));
            String destName   = extractFullName(coreService.depositAccountHolders(event.getDestination()));

            CoreBatchDepositTransferRequestDTO coreRequest =
                    buildCoreRequest(event, sourceName, destName);

            CoreBatchDepositTransferResponseDTO response =
                    coreService.batchDepositTransfer(coreRequest);

            boolean isSuccess =
                    response != null &&
                            response.getStatus() != null &&
                            "200".equals(response.getStatus().getCode());

            if (isSuccess) {
                log.info("SUCCESS → {}", event.getTransactionId());

                commandGateway.send(new BatchDepositTransferSuccessCommand(
                        event.getTransactionId(),
                        response.getResult().getData().getTransactionCode(),
                        response.getResult().getData().getTransactionDate()
                ));
                return;
            }

            log.error("FAIL → {}", event.getTransactionId());

            commandGateway.send(new BatchDepositTransferFailCommand(
                    event.getTransactionId()
            ));
        }

        catch (FeignException.FeignClientException timeout) {

            if (timeout.status() == 408) {
                log.warn("TIMEOUT → Running inquiry for {}", event.getTransactionId());

                var inquiry = coreService.transactionInquiry(event.getTransactionId());

                commandGateway.send(new BatchDepositTransferInquiredCommand(
                        event.getTransactionId(),
                        inquiry.getResult().getData().getTransactionCode(),
                        inquiry.getResult().getData().getTransactionDate(),
                        mapStatus(inquiry.getResult().getData().getTransactionStatus()),
                        inquiry.getResult().getData().getTransactionStatus()
                ));
            } else {
                // Other 4xx → Fail
                commandGateway.send(new BatchDepositTransferFailCommand(
                        event.getTransactionId()
                ));
            }
        }

        catch (FeignException.FeignServerException serverError) {

            log.error("SERVER ERROR → {}", serverError.getMessage());

            commandGateway.send(new BatchDepositTransferFailCommand(
                    event.getTransactionId()
            ));
        }

        catch (Exception ex) {
            log.error("UNEXPECTED ERROR → {}", ex.getMessage());
            commandGateway.send(new BatchDepositTransferFailCommand(
                    event.getTransactionId()
            ));
        }
    }

    private String extractFullName(CoreDepositAccountHoldersResponseDTO res) {
        if (res == null ||
                res.getResult() == null ||
                res.getResult().getData() == null ||
                res.getResult().getData().getDepositOwnerInfos() == null ||
                res.getResult().getData().getDepositOwnerInfos().isEmpty()) {
            return "";
        }
        var owner = res.getResult().getData().getDepositOwnerInfos().get(0);
        return owner.getFirstName() + " " + owner.getLastName();
    }


    private CoreBatchDepositTransferRequestDTO buildCoreRequest(
            BatchDepositTransferRequestedEvent e,
            String sourceName,
            String destName
    ) {
        CreditorDTO creditor = CreditorDTO.builder()
                .destinationAccount(e.getDestination())
                .documentItemType("Deposit")
                .branchCode("64")
                .destinationAmount(e.getAmount())
                .destinationComment(destName)
                .build();

        return CoreBatchDepositTransferRequestDTO.builder()
                .sourceAccount(e.getSource())
                .documentItemType("Deposit")
                .branchCode("64")
                .sourceAmount(e.getAmount())
                .sourceComment(sourceName)
                .transferBillNumber(
                        e.getExtraInformation() != null
                                ? (String) e.getExtraInformation().getOrDefault("transferBillNumber", "")
                                : ""
                )
                .transactionId(e.getTransactionId())
                .creditors(List.of(creditor))
                .build();
    }

    private TransactionResponseStatus mapStatus(String status) {
        if (status == null) return TransactionResponseStatus.INPROGRESS;

        switch (status.toUpperCase()) {
            case "SUCCESS": return TransactionResponseStatus.SUCCESS;
            case "FAILED":
            case "UNSUCCESS": return TransactionResponseStatus.UNSUCCESS;
            case "REVERSED": return TransactionResponseStatus.REVERSED;
            default: return TransactionResponseStatus.INPROGRESS;
        }
    }
}
