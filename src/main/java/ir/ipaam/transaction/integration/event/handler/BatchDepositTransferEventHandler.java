package ir.ipaam.transaction.integration.event.handler;

import feign.FeignException;
import ir.ipaam.transaction.api.read.dto.GetTransactionStatusQuery;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.command.BatchDepositTransferFailCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferInquiredCommand;
import ir.ipaam.transaction.application.command.BatchDepositTransferSuccessCommand;
import ir.ipaam.transaction.domain.event.BatchDepositTransferRequestedEvent;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreDepositAccountHoldersResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ProcessingGroup("transaction-handler")
public class BatchDepositTransferEventHandler {

    private final CommandGateway commandGateway;
    private final CoreService coreService;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void on(BatchDepositTransferRequestedEvent event) {

        try {
            String sourceName = extractFullName(coreService.depositAccountHolders(event.getSource()));
            String destName   = extractFullName(coreService.depositAccountHolders(event.getDestination()));

            CoreBatchDepositTransferRequestDTO coreRequest =
                    buildCoreRequest(event, sourceName, destName);

            BatchDepositTransferResponseDTO response =
                    coreService.batchDepositTransfer(coreRequest);

            boolean success =
                    response != null &&
                            response.getStatus() != null &&
                            "200".equals(response.getStatus().getCode());

            if (success) {
                queryUpdateEmitter.emit(
                        GetTransactionStatusQuery.class,
                        q -> q.getTransactionId().equals(event.getTransactionId()),
                        response
                );
                commandGateway.send(new BatchDepositTransferSuccessCommand(
                        event.getTransactionId(),
                        response.getResult().getData().getTransactionCode(),
                        response.getResult().getData().getTransactionDate()
                ));
            }else {
                emitFailure(event.getTransactionId());
                commandGateway.send(new BatchDepositTransferFailCommand(event.getTransactionId()));
            }}

        catch (FeignException.FeignClientException timeout) {

            if (timeout.status() == 408) {

                BatchDepositTransferResponseDTO inquiryResponse =
                        coreService.transactionInquiry(event.getTransactionId());
                queryUpdateEmitter.emit(
                        GetTransactionStatusQuery.class,
                        q -> q.getTransactionId().equals(event.getTransactionId()),
                        inquiryResponse
                );
                commandGateway.send(new BatchDepositTransferInquiredCommand(
                        event.getTransactionId(),
                        inquiryResponse.getResult().getData().getTransactionCode(),
                        inquiryResponse.getResult().getData().getTransactionDate(),
                        mapStatus(inquiryResponse.getResult().getData().getTransactionStatus()),
                        inquiryResponse.getResult().getData().getTransactionStatus()
                ));

            } else {
                emitFailure(event.getTransactionId());
                commandGateway.send(new BatchDepositTransferFailCommand(event.getTransactionId()));
            }
        }

        catch (FeignException.FeignServerException serverError) {
            commandGateway.send(new BatchDepositTransferFailCommand(event.getTransactionId()));

        }
        catch (Exception ex) {
            emitFailure(event.getTransactionId());
            commandGateway.send(new BatchDepositTransferFailCommand(event.getTransactionId()));
        }
    }

    private void emitFailure(String transactionId) {
        BatchDepositTransferResponseDTO failed = BatchDepositTransferResponseDTO.builder()
                .status(BatchDepositTransferResponseDTO.Status.builder()
                        .code("500")
                        .message("FAILED")
                        .description("Core service error")
                        .build())
                .result(null)
                .meta(BatchDepositTransferResponseDTO.Meta.builder()
                        .transactionId(transactionId)
                        .build())
                .build();

        queryUpdateEmitter.emit(
                GetTransactionStatusQuery.class,
                q -> q.getTransactionId().equals(transactionId),
                failed
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
