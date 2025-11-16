package ir.ipaam.transaction.api.write.controller;

import ir.ipaam.transaction.application.service.TransactionService;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.query.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit/batch")
    public ResponseEntity<?> batchTransfer(@RequestBody CoreBatchDepositTransferRequestDTO request) {
        String transactionId = transactionService.startBatchTransfer(request);
        return ResponseEntity.ok(Map.of("transactionId", transactionId));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }
}

