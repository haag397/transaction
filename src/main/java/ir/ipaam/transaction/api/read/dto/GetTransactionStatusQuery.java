package ir.ipaam.transaction.api.read.dto;

import lombok.Value;

@Value
public class GetTransactionStatusQuery {
    String transactionId;
}