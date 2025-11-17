package ir.ipaam.transaction.integration.client.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreDepositAccountHoldersResponseDTO {

    private Result result;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Result {
        private Data data;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Data {
        private List<OwnerInfo> depositOwnerInfos;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OwnerInfo {
        private String firstName;
        private String lastName;
    }
}
