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
public class PolTransferRequestDTO {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    private String transactionId;

    @NotNull(message = "Identifier type is required")
    private Integer identifierType;

    @NotBlank(message = "Customer number is required")
    private String customerNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Long amount;

    @NotBlank(message = "Destination IBAN is required")
    private String destIban;

    @NotNull(message = "Terminal type is required")
    private Integer terminalType;

    private String description;

    @NotNull(message = "Purpose code is required")
    private Integer purposeCode;

    private Boolean withOutInquiry;

    @NotBlank(message = "Creditor full name is required")
    private String creditorFullName;

    @NotBlank(message = "Payment id is required")
    private String paymentId;

    @NotBlank(message = "Effective date is required")
    private String effectiveDate;
}
