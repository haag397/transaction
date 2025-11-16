package ir.ipaam.transaction.query.model;

import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
import ir.ipaam.transaction.domain.model.TransactionSubType;
import ir.ipaam.transaction.domain.model.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "\"transaction\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "source")
    private String source; //source deposit card paya:sheba number deposit number

    @Column(name = "source_title")
    private String sourceTitle;

    @Column(name = "destination")
    private String destination;//dest deposit card paya:sheba number deposit number

    @Column(name = "destination_title")
    private String destinationTitle;//receiver name

    @Column(name = "amount")
    private Long amount;

    @Column(name = "description")
    private String description;

    @Column(name = "source_description")
    private String sourceDescription;

    @Column(name = "extra_description")
    private String extraDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> extraInformation;

    @Column(name = "reason")
    private String reason; //reason

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionResponseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type; //paya satna pol card to cart account_transfer

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_type")
    private TransactionSubType subType; // category of transaction like safte charge

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "transaction_code")
    private String transactionCode;

    @Column(name = "ref_number")
    private String refNumber;

    @Column(name = "error_message")
    private String errorMessage;
}
