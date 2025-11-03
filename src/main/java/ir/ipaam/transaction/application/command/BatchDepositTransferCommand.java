package ir.ipaam.transaction.application.command;

import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Value
public class BatchDepositTransferCommand {
    @TargetAggregateIdentifier
    String transactionId;
    String documentItemType;
    String sourceAccount;
    String branchCode;
    Long sourceAmount;
    String sourceComment;
    String transferBillNumber;
    List<CreditorDTO> creditors;
    String transactionCode;
    String transactionDate;
}
