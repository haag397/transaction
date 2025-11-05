package ir.ipaam.transaction.integration.client.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import ir.ipaam.common.exception.CustomBusinessException;
import ir.ipaam.transaction.integration.client.core.CoreClient;
import ir.ipaam.transaction.integration.client.core.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreServiceImpl implements CoreService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final CoreClient coreClient;

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
    public CoreTransactionInquiryResponseDTO transactionInquiry(String transactionId) {
        try {
            return coreClient.transactionInquiry(transactionId);
        } catch (FeignException feignException) {
            handleFeignException(feignException, "خطا در فراخوانی سرویس استعالم تراکنش");
        }
        return null;
    }

    @Override
    public CoreSatnaTransferResponseDTO satnaTransfer(CoreSatnaTransferRequestDTO request) {
        try {
            return coreClient.satnaTransfer(request);
        } catch (FeignException feignException) {
            handleFeignException(feignException, "خطا در فراخوانی سرویس انتقال وجه ساتنا");
        }
        return null;
    }

    @Override
    public CorePolTransferResponseDTO polTransfer(CorePolTransferRequestDTO request) {
        try {
            return coreClient.polTransfer(request);
        } catch (FeignException feignException) {
            handleFeignException(feignException, "خطا در فراخوانی سرویس انتقال وجه پون");
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
