package ir.ipaam.transaction.api.write.controller;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.service.BatchDepositTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction/batch-deposit-transfer")
@RequiredArgsConstructor
public class BatchDepositTransferController {

    private final BatchDepositTransferService batchDepositTransferService;

    @PostMapping
    @Operation(summary = "Create batch deposit transfer",
            description = "Initiates a batch deposit transfer transaction")
    public ResponseEntity<BatchDepositTransferResponseDTO> createBatchDepositTransfer(
            @Valid @RequestBody BatchDepositTransferRequestDTO request) {

        BatchDepositTransferResponseDTO response = batchDepositTransferService
                .createBatchDepositTransfer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
