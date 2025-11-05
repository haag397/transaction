package ir.ipaam.transaction.query.model;

import ir.ipaam.transaction.domain.model.DetailType;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
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
@Table(name = "\"Transaction\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @Column(nullable = false)
    private UUID id;

    private String transactionId;
    private String source; //source deposit card paya:sheba number deposit number
    private String destination;//dest deposit card paya:sheba number deposit number
    private String destinationTitle;//receiver name
    private Long amount;

    private String description;
    private String sourceDescription;
    private String extraDescription;

    private String destinationAccount;
    private Long destinationAmount;
    private String destinationComment;

    private TransactionResponseStatus status;

    private TransactionType type; //paya satna pol card to cart account_transfer
    private TransactionSubType subType; // category of transaction like safte charge

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> extraInformation;

    private DetailType detail; //reason
    private String senderReturnDepositNumber; //شماره سپرده بازگشت وجه
    private String transactionChannelType;//INTERNET MOBILE TELLER TELEPHONE

    private LocalDateTime transactionDate;
    private String transactionCode;
}
