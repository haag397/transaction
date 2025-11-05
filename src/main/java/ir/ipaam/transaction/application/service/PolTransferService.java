package ir.ipaam.transaction.application.service;

import ir.ipaam.transaction.api.write.dto.PolTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.PolTransferResponseDTO;
import ir.ipaam.transaction.application.command.PolTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CorePolTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CorePolTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.service.CoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PolTransferService {

    private final CommandGateway commandGateway;
    private final CoreService coreService;

    public PolTransferResponseDTO createPonTransfer(PolTransferRequestDTO request) {
        String transactionId = request.getTransactionId();
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        Boolean withOutInquiry = request.getWithOutInquiry() != null ? request.getWithOutInquiry() : Boolean.FALSE;

        CorePolTransferRequestDTO coreRequest = CorePolTransferRequestDTO.builder()
                .identifier(request.getIdentifier())
                .transactionId(transactionId)
                .identifierType(request.getIdentifierType())
                .customerNumber(request.getCustomerNumber())
                .amount(request.getAmount())
                .destIban(request.getDestIban())
                .terminalType(request.getTerminalType())
                .description(request.getDescription())
                .purposeCode(request.getPurposeCode())
                .withOutInquiry(withOutInquiry)
                .creditorFullName(request.getCreditorFullName())
                .paymentId(request.getPaymentId())
                .effectiveDate(request.getEffectiveDate())
                .build();

        CorePolTransferResponseDTO coreResponse = coreService.polTransfer(coreRequest);

        if (coreResponse == null) {
            throw new RuntimeException("Failed to process PON transfer");
        }

        String finalTransactionId = coreResponse.getTransactionId() != null ? coreResponse.getTransactionId() : transactionId;

        PolTransferCommand command = new PolTransferCommand(
                finalTransactionId,
                request.getIdentifier(),
                request.getIdentifierType(),
                request.getCustomerNumber(),
                request.getAmount(),
                request.getDestIban(),
                request.getTerminalType(),
                request.getDescription(),
                request.getPurposeCode(),
                request.getWithOutInquiry(),
                request.getCreditorFullName(),
                request.getPaymentId(),
                request.getEffectiveDate(),
                coreResponse.getReferenceNumber()
        );

        commandGateway.sendAndWait(command);

        log.info("PON transfer initiated successfully for transactionId: {}", finalTransactionId);

        return PolTransferResponseDTO.builder()
                .transactionId(finalTransactionId)
                .referenceNumber(coreResponse.getReferenceNumber())
                .status(coreResponse.getStatus())
                .message(coreResponse.getMessage())
                .build();
    }
}

