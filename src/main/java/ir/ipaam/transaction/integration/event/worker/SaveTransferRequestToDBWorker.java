package ir.ipaam.transaction.integration.event.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import ir.ipaam.transaction.application.command.BatchDepositTransferCommand;
import ir.ipaam.transaction.integration.client.core.dto.CoreBatchDepositTransferRequestDTO;
import ir.ipaam.transaction.integration.client.core.dto.CreditorDTO;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateStreamCreationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ir.ipaam.transaction.utills.TransactionIdGenerator.generate;

@Component
@AllArgsConstructor
public class SaveTransferRequestToDBWorker {

    private final CommandGateway commandGateway;

    @JobWorker(type = "save_to_db")
    public Map<String, Object> saveTransferRequestToDB(
            @Variable CoreBatchDepositTransferRequestDTO coreBatchDepositTransferRequestDTO,
            @Variable(name = "transactionId") String transactionIdVar) {

            List<CreditorDTO> creditors = coreBatchDepositTransferRequestDTO.getCreditors();
            String destination = null;
            String destinationTitle = null;

            CreditorDTO firstCreditor = creditors.get(0);
            destination = firstCreditor.getDestinationAccount();
            destinationTitle = firstCreditor.getDestinationComment();


            BatchDepositTransferCommand command = BatchDepositTransferCommand.builder()
                    .transactionId(transactionIdVar)
                    .source(coreBatchDepositTransferRequestDTO.getSourceAccount())
                    .destination(destination)
                    .destinationTitle(destinationTitle)
                    .amount(coreBatchDepositTransferRequestDTO.getSourceAmount())
                    .description(coreBatchDepositTransferRequestDTO.getSourceComment())
//                    .extraDescription(extraDescription)
//                    .extraInformation(extraInformation)
                    .build();

                commandGateway.sendAndWait(command);

        return Map.of(
                "transactionId", transactionIdVar,
                "status", "REQUESTED"
        );
    }
}
