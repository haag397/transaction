package ir.ipaam.transaction.integration.client.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoreBatchDepositTransferRequestDTO {
    String documentItemType;
    String sourceAccount;
    String branchCode;
    String sourceAmount;
    String sourceComment;
    String transferBillNumber;
    String transactionId;
    private List<CreditorDTO> creditors;

}