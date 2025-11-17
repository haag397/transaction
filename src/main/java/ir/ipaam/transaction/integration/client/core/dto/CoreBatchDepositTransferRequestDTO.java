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
    private String sourceAccount;
    private String documentItemType;
    private String branchCode;
    private Long sourceAmount;
    private String sourceComment;
    private String transferBillNumber;
    private String transactionId;
    private List<CreditorDTO> creditors;
}