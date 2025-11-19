package ir.ipaam.transaction.application.service;

import lombok.Value;

@Value
public class GetTransactionStatusQuery {
    String transactionId;
}