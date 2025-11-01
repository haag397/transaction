package ir.ipaam.transaction.integration.client.core;

import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "core-client", url = "http://localhost:8290/api/corebanking/transaction")
public interface CoreClient {
    @PostMapping("/issuedocument")
    CoreBatchDepositTransferResponseDTO batchDepositTransfer(@RequestBody CoreBatchDepositTransferRequestDTO request);
    CoreTransactionInquiryResponseDTO transactionInquiry(@RequestBody CoreTransactionInquiryRequestDTO request);
}
