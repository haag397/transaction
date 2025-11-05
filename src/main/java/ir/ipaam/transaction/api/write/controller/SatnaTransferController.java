package ir.ipaam.transaction.api.write.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.ipaam.transaction.api.write.dto.SatnaTransferRequestDTO;
import ir.ipaam.transaction.api.write.dto.SatnaTransferResponseDTO;
import ir.ipaam.transaction.application.service.SatnaTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction/satna-transfer")
@RequiredArgsConstructor
@Tag(name = "SATNA Transfer", description = "API for SATNA inter-bank transfer operations")
public class SatnaTransferController {

    private final SatnaTransferService satnaTransferService;

    @PostMapping
    @Operation(summary = "Create SATNA transfer",
            description = "Initiates a SATNA inter-bank transfer transaction")
    public ResponseEntity<SatnaTransferResponseDTO> createSatnaTransfer(
            @Valid @RequestBody SatnaTransferRequestDTO request) {

        SatnaTransferResponseDTO response = satnaTransferService
                .createSatnaTransfer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


