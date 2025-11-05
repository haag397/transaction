package ir.ipaam.transaction.integration.client.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreSatnaTransferRequestDTO {
    private Long amount;
    private String destinationDepNum;
    private String receiverName;
    private String receiverLastName;
    private String transactionDate;
    private String sourceDepNum;
    private String description;
    private String transactionId;
    private String detailType;
    private Boolean isAutoVerify;
    private String transactionBillNumber;
    private String senderReturnDepositNumber;
    private String senderCustomerNumber;
    private String commissionDepositNumber;
    private String destBankCode;
    private String senderPostalCode;
    private String senderNationalCode;
    private String senderShahabCode;
    private String senderNameOrCompanyType;
    private String senderFamilyNameOrCompanyName;
    private Boolean isFromBox;
}


