package ir.ipaam.transaction.api.write.controller;

import ir.ipaam.transaction.api.write.dto.PolTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.PolTransferResponseDTO;
import ir.ipaam.transaction.application.service.PolTransferService;
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
@RequestMapping("/api/transaction/pon-transfer")
@RequiredArgsConstructor
@Tag(name = "PON Transfer", description = "API for PON transfer operations")
public class PonTransferController {

    private final PolTransferService polTransferService;

    @PostMapping
    @Operation(summary = "Create PON transfer",
            description = "Initiates a PON transfer transaction")
    public ResponseEntity<PolTransferResponseDTO> createPonTransfer(
            @Valid @RequestBody PolTransferRequestDTO request) {

        PolTransferResponseDTO response = polTransferService.createPonTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
