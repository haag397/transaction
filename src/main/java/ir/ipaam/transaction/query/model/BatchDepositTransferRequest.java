package ir.ipaam.transaction.query.model;

import ir.ipaam.transaction.domain.model.BatchDepositTransferStatus;
import ir.ipaam.transaction.domain.model.TransactionResponseStatus;
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
    private String transferBillNumber;
    private String sourceAccount;
    private String documentItemType;
    private Long sourceAmount;
    private String sourceComment;
    private String branchCode;

    @Column(name = "creditors", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<CreditorDTO> creditors;

    private Boolean isSuccess;
    private LocalDateTime transactionDate;
    private String transactionCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private BatchDepositTransferStatus status;
    private TransactionResponseStatus resultData;
}
