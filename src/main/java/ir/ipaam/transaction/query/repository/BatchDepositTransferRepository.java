package ir.ipaam.transaction.query.repository;

import ir.ipaam.transaction.query.model.BatchDepositTransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BatchDepositTransferRepository extends JpaRepository<BatchDepositTransferRequest, UUID> {

}