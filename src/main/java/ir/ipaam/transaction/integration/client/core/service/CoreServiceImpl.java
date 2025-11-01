package ir.ipaam.transaction.integration.client.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import ir.ipaam.common.exception.CustomBusinessException;
import ir.ipaam.transaction.integration.client.core.CoreClient;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferResponseDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CoreTransactionInquiryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreServiceImpl implements CoreService {

    private final CoreClient coreClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CoreBatchDepositTransferResponseDTO batchDepositTransfer(CoreBatchDepositTransferRequestDTO request) {
        try {
            return coreClient.batchDepositTransfer(request);
        } catch (FeignException feignException) {
            handleFeignException(feignException, "خطا در فراخوانی سرویس انتقال وجه حساب به حساب گروهی");
        }
        return null;
    }

    @Override
    public CoreTransactionInquiryResponseDTO transactionInquiry(CoreTransactionInquiryRequestDTO request) {
        try {
            return coreClient.transactionInquiry(request);
        } catch (FeignException feignException) {
            handleFeignException(feignException, "خطا در فراخوانی سرویس استعالم تراکنش");
        }
        return null;
    }

    private void handleFeignException(FeignException feignException, String defaultMessage) {
        String responseBody = feignException.contentUTF8();
        int httpStatus = feignException.status();

        if (httpStatus >= 500 && httpStatus < 600) {
            throw new RuntimeException(defaultMessage);
        }

        String message;
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            message = root.path("result").path("status").path("message").asText(defaultMessage);
        } catch (Exception parseException) {
            message = defaultMessage;
        }

        throw new CustomBusinessException(message);
    }
}
