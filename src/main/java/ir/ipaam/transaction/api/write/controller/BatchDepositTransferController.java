package ir.ipaam.transaction.api.write.controller;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.application.service.BatchDepositTransferService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/transaction/batch-deposit-transfer")
@RequiredArgsConstructor
public class BatchDepositTransferController {

    private final BatchDepositTransferService batchDepositTransferService;

    @PostMapping
    public CompletableFuture<ResponseEntity<BatchDepositTransferResponseDTO>> createBatchDepositTransfer(
            @Valid @RequestBody BatchDepositTransferRequestDTO request) {

        return batchDepositTransferService
                .createBatchDepositTransfer(request)
                .thenApply(ResponseEntity::ok);
    }
}
