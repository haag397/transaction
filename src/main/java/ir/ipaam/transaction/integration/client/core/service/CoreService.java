package ir.ipaam.transaction.integration.client.core.service;

import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;

public interface CoreService {
    CoreBatchDepositTransferResponseDTO batchDepositTransfer(CoreBatchDepositTransferRequestDTO request);
}
