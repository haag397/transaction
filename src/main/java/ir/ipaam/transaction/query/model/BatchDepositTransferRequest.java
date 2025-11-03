package ir.ipaam.transaction.query.model;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransferType;
import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "\"BatchDepositTransferRequest\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDepositTransferRequest {
    @Id
    @Column(nullable = false)
    private UUID id;

    private String transactionId;
//    private String transferBillNumber;
    private String sourceInstrument; //source deposit card paya:sheba number deposit number
    private String documentItemType;
    private Long amount;
    //TODO have this or not
    private String sourceComment;
    private String branchCode;

    @Column(name = "creditors", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<CreditorDTO> creditors;

    private LocalDateTime transactionDate;
    private String transactionCode;

    private TransactionResponseStatus transactionResponseStatus;

    private TransferType transferType; //paya satna account to account
    private String description;
    private String extraDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> extraInformation;
    //paya
    private String recieverFullName;
    private String destinationIban; //شماره شبا مقصد
    private String detailType; //reason
    private String senderReturnDepositNumber; //شماره سپرده بازگشت وجه
    private String destBankCode; //کد بانک مقصد
    private String transactionChannelType;
    //satna
    private String recieverName;
    private String recieverLastName;
}
