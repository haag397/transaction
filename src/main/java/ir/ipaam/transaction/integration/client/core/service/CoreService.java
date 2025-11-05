package ir.ipaam.transaction.integration.client.core.service;

import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CorePolTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CorePolTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreSatnaTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreSatnaTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;

public interface CoreService {
    CoreBatchDepositTransferResponseDTO batchDepositTransfer(CoreBatchDepositTransferRequestDTO request);
    CoreTransactionInquiryResponseDTO transactionInquiry(CoreTransactionInquiryRequestDTO request);
    CoreSatnaTransferResponseDTO satnaTransfer(CoreSatnaTransferRequestDTO request);
    CorePolTransferResponseDTO polTransfer(CorePolTransferRequestDTO request);
}
