package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.api.write.dto.SatnaTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.SatnaTransferResponseDTO;
import ir.ipaam.transaction.application.command.SatnaTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreSatnaTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreSatnaTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SatnaTransferService {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    public SatnaTransferResponseDTO createSatnaTransfer(SatnaTransferRequestDTO request) {
        // Generate transaction ID
        String transactionId = UUID.randomUUID().toString();

        // Call core service first to process the transfer
        CoreSatnaTransferRequestDTO coreRequest = CoreSatnaTransferRequestDTO.builder()
                .transactionId(transactionId)
                .amount(request.getAmount())
                .destinationDepNum(request.getDestinationDepNum())
                .receiverName(request.getReceiverName())
                .receiverLastName(request.getReceiverLastName())
                .transactionDate(LocalDateTime.now(ZoneId.of("Asia/Tehran")).format(DateTimeFormatter.ISO_DATE_TIME))
                .sourceDepNum(request.getSourceDepNum())
                .description(request.getDescription())
                .detailType(request.getDetailType())
                .isAutoVerify(request.getIsAutoVerify())
                .transactionBillNumber(request.getTransactionBillNumber())
                .senderReturnDepositNumber(request.getSenderReturnDepositNumber())
                .senderCustomerNumber(request.getSenderCustomerNumber())
                .commissionDepositNumber(request.getCommissionDepositNumber())
                .destBankCode(request.getDestBankCode())
                .senderPostalCode(request.getSenderPostalCode())
                .senderNationalCode(request.getSenderNationalCode())
                .senderShahabCode(request.getSenderShahabCode())
                .senderNameOrCompanyType(request.getSenderNameOrCompanyType())
                .senderFamilyNameOrCompanyName(request.getSenderFamilyNameOrCompanyName())
                .isFromBox(request.getIsFromBox())
                .build();

        CoreSatnaTransferResponseDTO coreResponse = coreService.satnaTransfer(coreRequest);

        if (coreResponse == null) {
            throw new RuntimeException("Failed to process SATNA transfer");
        }

        // Create and send command to SATNA aggregate
        SatnaTransferCommand command = new SatnaTransferCommand(
                coreResponse.getTransactionId() != null ? coreResponse.getTransactionId() : transactionId,
                request.getAmount(),
                request.getDestinationDepNum(),
                request.getReceiverName(),
                request.getReceiverLastName(),
                request.getSourceDepNum(),
                request.getDescription(),
                request.getDetailType(),
                request.getIsAutoVerify(),
                request.getTransactionBillNumber(),
                request.getSenderReturnDepositNumber(),
                request.getSenderCustomerNumber(),
                request.getCommissionDepositNumber(),
                request.getDestBankCode(),
                request.getSenderPostalCode(),
                request.getSenderNationalCode(),
                request.getSenderShahabCode(),
                request.getSenderNameOrCompanyType(),
                request.getSenderFamilyNameOrCompanyName(),
                request.getIsFromBox(),
                coreResponse.getTransactionCode(),
                coreResponse.getTransactionDate(),
                coreResponse.getUserReferenceNumber()
        );

        commandGateway.sendAndWait(command);

        return SatnaTransferResponseDTO.builder()
                .transactionId(coreResponse.getTransactionId())
                .transactionDate(coreResponse.getTransactionDate())
                .transactionCode(coreResponse.getTransactionCode())
                .amount(coreResponse.getAmount())
                .receiverName(coreResponse.getRecieverName())
                .receiverLastName(coreResponse.getRecieverlastName())
                .destinationDepNum(coreResponse.getDestinationDepNum())
                .userReferenceNumber(coreResponse.getUserReferenceNumber())
                .status("SUCCESS")
                .message("SATNA transfer initiated successfully")
                .build();
    }
}


