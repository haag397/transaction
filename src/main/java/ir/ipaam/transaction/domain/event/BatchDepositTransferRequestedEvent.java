package ir.ipaam.transaction.domain.event;

import lombok.*;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchDepositTransferRequestedEvent {
    String transactionId;
    String source;
    String sourceTitle;
    String destination;
    String destinationTitle;
    Long amount;
    String description;
    String sourceDescription;
    String extraDescription;
    Map<String, Object> extraInformation;
}
