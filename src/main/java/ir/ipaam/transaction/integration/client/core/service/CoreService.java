package ir.ipaam.transaction.integration.client.core.service;

import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;

public interface CoreService {
    CoreBatchDepositTransferResponseDTO batchDepositTransfer(CoreBatchDepositTransferRequestDTO request);
    CoreTransactionInquiryResponseDTO transactionInquiry(CoreTransactionInquiryRequestDTO request);
}
