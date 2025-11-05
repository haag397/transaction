package ir.ipaam.transaction.integration.client.core;

import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CorePolTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CorePolTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreSatnaTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreSatnaTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "core-client", url = "http://localhost:8290/api/corebanking/payment/v1.0")
public interface CoreClient {
    @PostMapping("/accounts/transfer")
    CoreBatchDepositTransferResponseDTO batchDepositTransfer(@RequestBody CoreBatchDepositTransferRequestDTO request);
    
    @GetMapping("/transaction/{transactionId}")
    CoreTransactionInquiryResponseDTO transactionInquiry(@PathVariable String transactionId);
    
    @PostMapping("/satna")
    CoreSatnaTransferResponseDTO satnaTransfer(@RequestBody CoreSatnaTransferRequestDTO request);

    @PostMapping("/pol")
    CorePolTransferResponseDTO polTransfer(@RequestBody CorePolTransferRequestDTO request);
}
