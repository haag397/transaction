package ir.ipaam.transaction.api.write.dto;

import ir.ipaam.transaction.domain.model.TransactionSubType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDepositTransferRequestDTO {
    @NotBlank(message = "مبدا اکانت خالی است")
    String source;

    @Size(max = 255, message = "کاراکتر بیش از حد مجاز")
    String description;

    @Size(max = 255, message = "کاراکتر بیش از حد مجاز")
    String sourceDescription;

    @Size(max = 50)
    String transferBillNumber;

    @NotBlank(message = "اکانت مقصد خالی است")
    String destination;

    @Size(max = 255)
    String reason;

    @NotNull(message = "مبلغ الزامی است")
    @Min(value = 1000, message = "حداقل مبلغ 1000 ریال است")
    Long amount;

    TransactionSubType subType;
}

