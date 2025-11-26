package ir.ipaam.transaction.integration.client.core.service;

import ir.ipaam.transaction.api.write.dto.BatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.*;

public interface CoreService {
    BatchDepositTransferResponseDTO batchDepositTransfer(CoreBatchDepositTransferRequestDTO request);
    BatchDepositTransferResponseDTO transactionInquiry(String transactionId);
    CoreSatnaTransferResponseDTO satnaTransfer(CoreSatnaTransferRequestDTO request);
    CorePolTransferResponseDTO polTransfer(CorePolTransferRequestDTO request);
    CoreDepositAccountHoldersResponseDTO depositAccountHolders(String depositNumber);
}
