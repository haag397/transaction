# Proper CQRS & Event Sourcing Implementation

## Overview
This implementation follows **proper Domain-Driven Design (DDD)** principles with **separate aggregates** for each transfer type, while maintaining a **unified read model** for querying.

## Architecture Principles

### ✅ Write Side (Command Side) - SEPARATED
Each transfer type has its own:
- **Command** - Type-specific command
- **Aggregate** - Type-specific business logic & validation
- **Events** - Type-specific domain events

### ✅ Read Side (Query Side) - UNIFIED
All transfer types share:
- **Entity** - Single `BatchDepositTransferRequest` table
- **Repository** - Single `BatchDepositTransferRepository`
- **Event Handler** - Unified projection that listens to ALL event types

## 📊 Architecture Diagram

```
┌──────────────────── WRITE SIDE (Separated) ────────────────────┐
│                                                                  │
│  Deposit Transfer:                                              │
│  BatchDepositTransferCommand → BatchDepositTransferAggregate    │
│                              ↓                                   │
│                   BatchDepositTransferedEvent                   │
│                                                                  │
│  SATNA Transfer:                                                │
│  SatnaTransferCommand → SatnaTransferAggregate                  │
│                        ↓                                         │
│            SatnaTransferredEvent                                │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
                               ↓
┌──────────────────── READ SIDE (Unified) ───────────────────────┐
│                                                                  │
│            BatchDepositTransferEventHandler                     │
│                    @EventHandler                                │
│           ┌──────────────┴──────────────┐                      │
│           │                              │                       │
│    on(BatchDepositTransferedEvent)  on(SatnaTransferredEvent)  │
│           │                              │                       │
│           └──────────────┬──────────────┘                      │
│                          ↓                                       │
│              BatchDepositTransferRequest                        │
│              (Unified Entity with transferType)                 │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

## 🎯 Components

### 1. Deposit Transfer (Write Side)

#### Command
```java
public class BatchDepositTransferCommand {
    @TargetAggregateIdentifier
    String transactionId;
    String documentItemType;
    String sourceAccount;
    Long sourceAmount;
    List<CreditorDTO> creditors;
    // ... deposit-specific fields
}
```

#### Aggregate
```java
@Aggregate
public class BatchDepositTransferAggregate {
    @AggregateIdentifier
    private String transactionId;
    private Long sourceAmount;
    private List<CreditorDTO> creditors;
    // ... deposit-specific state
    
    @CommandHandler
    public BatchDepositTransferAggregate(BatchDepositTransferCommand command) {
        validateDepositTransfer(command);  // Deposit-specific validation
        // ... set state
        AggregateLifecycle.apply(BatchDepositTransferedEvent.builder()...build());
    }
}
```

#### Event
```java
@Value
@Builder
public class BatchDepositTransferedEvent {
    String transactionId;
    String transactionCode;
    Long sourceAmount;
    TransactionResponseStatus transactionResponseStatus;
}
```

### 2. SATNA Transfer (Write Side)

#### Command
```java
public class SatnaTransferCommand {
    @TargetAggregateIdentifier
    String transactionId;
    Long amount;
    String destinationDepNum;
    String receiverName;
    String receiverLastName;
    // ... SATNA-specific fields
}
```

#### Aggregate
```java
@Aggregate
public class SatnaTransferAggregate {
    @AggregateIdentifier
    private String transactionId;
    private Long amount;
    private String receiverName;
    // ... SATNA-specific state
    
    @CommandHandler
    public SatnaTransferAggregate(SatnaTransferCommand command) {
        validateSatnaTransfer(command);  // SATNA-specific validation
        // ... set state
        AggregateLifecycle.apply(SatnaTransferredEvent.builder()...build());
    }
}
```

#### Event
```java
@Value
@Builder
public class SatnaTransferredEvent {
    String transactionId;
    String transactionCode;
    Long amount;
    String receiverName;
    String receiverLastName;
    String userReferenceNumber;
    TransactionResponseStatus transactionResponseStatus;
}
```

### 3. Unified Read Model (Query Side)

#### Entity (Single Table for All Types)
```java
@Entity
@Table(name = "\"BatchDepositTransferRequest\"")
public class BatchDepositTransferRequest {
    @Id
    private UUID id;
    private String transactionId;
    private TransferType transferType;  // ← Discriminator
    private Long amount;
    
    // Deposit-specific fields (null for SATNA)
    private List<CreditorDTO> creditors;
    
    // SATNA-specific fields (null for Deposit)
    private String recieverName;
    private String recieverLastName;
    
    // Common fields
    private TransactionResponseStatus transactionResponseStatus;
}
```

#### Unified Event Handler (Projection)
```java
@Component
public class BatchDepositTransferEventHandler {
    
    @EventHandler
    public void on(BatchDepositTransferedEvent event) {
        // Project Deposit event to unified entity
        BatchDepositTransferRequest entity = BatchDepositTransferRequest.builder()
            .transactionId(event.getTransactionId())
            .transferType(TransferType.ACCOUNT_TRANSFER)  // ← Set type
            .amount(event.getSourceAmount())
            .build();
        repository.save(entity);
    }
    
    @EventHandler
    public void on(SatnaTransferredEvent event) {
        // Project SATNA event to unified entity
        BatchDepositTransferRequest entity = BatchDepositTransferRequest.builder()
            .transactionId(event.getTransactionId())
            .transferType(TransferType.SATNA)  // ← Set type
            .amount(event.getAmount())
            .recieverName(event.getReceiverName())
            .build();
        repository.save(entity);
    }
}
```

## 📋 Benefits of This Approach

### ✅ Write Side Benefits (Separate Aggregates)

1. **Single Responsibility**
   - Each aggregate handles ONE type of transfer
   - Clear, focused business logic

2. **Type Safety**
   - Compile-time guarantee of correct command usage
   - No unnecessary fields in aggregates

3. **Independent Evolution**
   - Change SATNA without affecting Deposit
   - Different teams can own different aggregates

4. **Specific Validation**
   ```java
   // Deposit-specific
   if (creditors.isEmpty()) throw new Exception();
   
   // SATNA-specific
   if (!isValidBankCode(destBankCode)) throw new Exception();
   ```

5. **Clear Business Rules**
   - SATNA has different rules than Deposit
   - Each aggregate enforces its own invariants

### ✅ Read Side Benefits (Unified Projection)

1. **Unified Querying**
   ```java
   // Get all transfers for a customer
   repository.findByCustomerId(customerId);
   
   // Get all failed transfers regardless of type
   repository.findByStatus(UNSUCCESS);
   ```

2. **Unified Reporting**
   - Single table for reports
   - Easy analytics across all transfer types

3. **Simple Projections**
   - One repository instead of many
   - One database table

4. **Type Discrimination**
   ```sql
   SELECT * FROM "BatchDepositTransferRequest" 
   WHERE transfer_type = 'SATNA'
   ```

## 🔄 Data Flow Example

### SATNA Transfer Flow

```
1. Client → POST /api/transaction/satna-transfer
           {receiverName: "Ali", amount: 1000000}

2. SatnaTransferService
   ↓ creates SatnaTransferCommand

3. CommandGateway → SatnaTransferAggregate
   ↓ validates SATNA business rules
   ↓ applies SatnaTransferredEvent

4. Event Store
   ↓ persists SatnaTransferredEvent

5. BatchDepositTransferEventHandler
   ↓ @EventHandler on(SatnaTransferredEvent)
   ↓ creates BatchDepositTransferRequest
   ↓ sets transferType = SATNA

6. Database
   ↓ saves to BatchDepositTransferRequest table
   
7. Response ← SatnaTransferResponseDTO
```

## 📦 File Structure

```
application/
└── command/
    ├── BatchDepositTransferCommand.java  ← Deposit-specific
    ├── SatnaTransferCommand.java         ← SATNA-specific
    ├── UpdateDepositTransferStateCommand.java
    └── UpdateSatnaTransferStateCommand.java

domain/
├── aggregate/
│   ├── BatchDepositTransferAggregate.java  ← Deposit-specific
│   └── SatnaTransferAggregate.java         ← SATNA-specific
└── event/
    ├── BatchDepositTransferedEvent.java    ← Deposit-specific
    ├── SatnaTransferredEvent.java          ← SATNA-specific
    ├── DepositTransferStateUpdatedEvent.java
    └── SatnaTransferStateUpdatedEvent.java

query/
├── model/
│   └── BatchDepositTransferRequest.java    ← UNIFIED entity
└── repository/
    └── BatchDepositTransferRepository.java  ← UNIFIED repository

integration/
└── event/
    └── handler/
        └── BatchDepositTransferEventHandler.java  ← UNIFIED projection
            ├── @EventHandler on(BatchDepositTransferedEvent)
            └── @EventHandler on(SatnaTransferredEvent)
```

## 🎯 Key Patterns Applied

### 1. **CQRS** (Command Query Responsibility Segregation)
- **Write Model**: Separate aggregates per type
- **Read Model**: Unified entity for querying

### 2. **Event Sourcing**
- All state changes captured as events
- Events persisted in event store
- Aggregates reconstructed from events

### 3. **Domain-Driven Design (DDD)**
- Bounded contexts (Deposit vs SATNA)
- Aggregates as transaction boundaries
- Domain events for state changes

### 4. **Projection Pattern**
- Event handler projects events to read model
- Multiple event types → single entity
- Eventual consistency

## 📊 Database Schema

### Event Store (Axon Framework)
```sql
-- Automatically managed by Axon
domain_event_entry (
    aggregate_identifier,  -- transactionId
    type,                  -- BatchDepositTransferAggregate or SatnaTransferAggregate
    sequence_number,
    event_type,            -- BatchDepositTransferedEvent or SatnaTransferredEvent
    payload
)
```

### Read Model (Query Side)
```sql
CREATE TABLE "BatchDepositTransferRequest" (
    id UUID PRIMARY KEY,
    transaction_id VARCHAR(255),
    transfer_type VARCHAR(50),  -- 'ACCOUNT_TRANSFER' or 'SATNA'
    amount BIGINT,
    
    -- Deposit-specific (null for SATNA)
    creditors JSONB,
    
    -- SATNA-specific (null for Deposit)
    reciever_name VARCHAR(255),
    reciever_last_name VARCHAR(255),
    
    -- Common
    transaction_response_status VARCHAR(50)
);
```

## ✨ Example Queries

### Get All SATNA Transfers
```java
repository.findAll().stream()
    .filter(t -> t.getTransferType() == TransferType.SATNA)
    .collect(Collectors.toList());
```

### Get Transaction History (All Types)
```java
repository.findByTransactionId(transactionId);
// Returns: Could be Deposit or SATNA, discriminated by transactionType
```

### Get Failed Transfers Across All Types
```java
repository.findAll().stream()
    .filter(t -> t.getTransactionResponseStatus() == TransactionResponseStatus.UNSUCCESS)
    .collect(Collectors.toList());
```

## 🚀 Adding New Transfer Type (PAYA)

1. **Create Command**
```java
public class PayaTransferCommand { ... }
```

2. **Create Events**
```java
public class PayaTransferredEvent { ... }
```

3. **Create Aggregate**
```java
@Aggregate
public class PayaTransferAggregate { ... }
```

4. **Update Event Handler**
```java
@EventHandler
public void on(PayaTransferredEvent event) {
    BatchDepositTransferRequest entity = ...
    entity.setTransferType(TransferType.PAYA);
    repository.save(entity);
}
```

That's it! No changes to existing aggregates needed!

## 📝 Summary

### Write Side (Separated)
✅ `BatchDepositTransferCommand` → `BatchDepositTransferAggregate` → `BatchDepositTransferedEvent`  
✅ `SatnaTransferCommand` → `SatnaTransferAggregate` → `SatnaTransferredEvent`

### Read Side (Unified)
✅ `BatchDepositTransferEventHandler` listens to ALL events  
✅ Projects to single `BatchDepositTransferRequest` entity  
✅ `transferType` field discriminates between types

This is **proper CQRS/Event Sourcing** with **DDD best practices**! 🎉

