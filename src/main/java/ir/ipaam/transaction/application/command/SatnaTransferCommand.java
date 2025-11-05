package ir.ipaam.transaction.application.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SatnaTransferCommand {
    @TargetAggregateIdentifier
    String transactionId;
    Long amount;
    String destinationDepNum;
    String receiverName;
    String receiverLastName;
    String sourceDepNum;
    String description;
    String detailType;
    Boolean isAutoVerify;
    String transactionBillNumber;
    String senderReturnDepositNumber;
    String senderCustomerNumber;
    String commissionDepositNumber;
    String destBankCode;
    String senderPostalCode;
    String senderNationalCode;
    String senderShahabCode;
    String senderNameOrCompanyType;
    String senderFamilyNameOrCompanyName;
    Boolean isFromBox;
    String transactionCode;
    String transactionDate;
    String userReferenceNumber;
}

