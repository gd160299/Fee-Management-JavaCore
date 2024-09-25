package org.pj.Entity;


import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FEE_TRANSACTION")
@Data
public class FeeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fee_transaction_seq_gen")
    @SequenceGenerator(name = "fee_transaction_seq_gen", sequenceName = "FEE_TRANSACTION_SEQ", allocationSize = 1)
    private Long id;
    private String transactionCode;
    private String commandCode;
    private BigDecimal feeAmount;
    private String status;
    private String accountNumber;
    private int totalScan;
    private String remark;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
