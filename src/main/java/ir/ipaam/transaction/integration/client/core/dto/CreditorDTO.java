package ir.ipaam.transaction.integration.client.core.dto;

import lombok.Data;

@Data
public class CreditorDTO {
    private String destinationAccount;
    private String documentItemType;
    private String branchCode;
    private String personnelCode;
    private String destinationAmount;
    private String destinationComment;
}