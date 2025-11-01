package ir.ipaam.transaction.integration.client.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreBatchDepositTransferRequestDTO {
    String documentItemType;
    String sourceAccount;
    String branchCode;
    Long sourceAmount;
    String sourceComment;
    String transferBillNumber;
    String transactionId;
    List<CreditorDTO> creditors;
}