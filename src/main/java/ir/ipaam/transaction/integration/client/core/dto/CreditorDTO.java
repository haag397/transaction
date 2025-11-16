package ir.ipaam.transaction.integration.client.core.dto;

import lombok.Data;

@Data
public class CreditorDTO {
    private String destinationAccount;
    private String documentItemType;
    private String branchCode;
    private Long destinationAmount;
    private String destinationComment;
}