package ir.ipaam.transaction.api.write.dto;

import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDepositTransferRequestDTO {

    private String transactionId;
//    @NotBlank(message = "Document item type is required")
    private String documentItemType;
    
//    @NotBlank(message = "Source account is required")
    private String sourceAccount;
    
//    @NotBlank(message = "Branch code is required")
    private String branchCode;
    
//    @NotNull(message = "Source amount is required")
//    @Positive(message = "Source amount must be positive")
    private Long sourceAmount;
    
    private String sourceComment;

//    @NotEmpty(message = "Creditors list cannot be empty")
    private List<CreditorDTO> creditors;
    
    // Transaction type: "deposit-to-deposit", "pol", "satna", "paya"
    // Defaults to "deposit-to-deposit" for batch deposit transfers
    private String type;
}

