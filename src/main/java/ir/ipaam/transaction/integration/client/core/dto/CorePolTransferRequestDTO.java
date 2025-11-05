package ir.ipaam.transaction.integration.client.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorePolTransferRequestDTO {
    private String identifier;
    private String transactionId;
    private Integer identifierType;
    private String customerNumber;
    private Long amount;
    private String destIban;
    private Integer terminalType;
    private String description;
    private Integer purposeCode;
    private Boolean withOutInquiry;
    private String creditorFullName;
    private String paymentId;
    private String effectiveDate;
}
