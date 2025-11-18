package ir.ipaam.transaction.integration.client.core;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "core-client", url = "http://localhost:8290/api/corebanking")
public interface CoreClient {
    @PostMapping("/payment/v1.0/accounts/transfer")
//    CoreBatchDepositTransferResponseDTO batchDepositTransfer(@RequestBody CoreBatchDepositTransferRequestDTO request);
    BatchDepositTransferResponseDTO batchDepositTransfer(@RequestBody CoreBatchDepositTransferRequestDTO request);


    @GetMapping("/payment/v1.0/transaction/{transactionId}")
//    CoreTransactionInquiryResponseDTO transactionInquiry(@PathVariable String transactionId);
    BatchDepositTransferResponseDTO transactionInquiry(@PathVariable String transactionId);

    @GetMapping("/deposits/v1.0/holders/{depositNumber}")
    CoreDepositAccountHoldersResponseDTO depositAccountHolders(@PathVariable String depositNumber);
    
    @PostMapping("/payment/v1.0/satna")
    CoreSatnaTransferResponseDTO satnaTransfer(@RequestBody CoreSatnaTransferRequestDTO request);

    @PostMapping("/payment/v1.0/pol")
    CorePolTransferResponseDTO polTransfer(@RequestBody CorePolTransferRequestDTO request);
}
