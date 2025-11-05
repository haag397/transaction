package ir.ipaam.transaction.api.write.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatnaTransferRequestDTO {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Long amount;
    
    @NotBlank(message = "Destination deposit number is required")
    private String destinationDepNum;
    
    @NotBlank(message = "Receiver name is required")
    private String receiverName;
    
    @NotBlank(message = "Receiver last name is required")
    private String receiverLastName;
    
    @NotBlank(message = "Source deposit number is required")
    private String sourceDepNum;
    
    private String description;
    
    @NotBlank(message = "Detail type is required")
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


