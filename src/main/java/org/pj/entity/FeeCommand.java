package org.pj.entity;

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
@Table(name = "FEE_COMMAND")
@Data
public class FeeCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fee_command_seq_gen")
    @SequenceGenerator(name = "fee_command_seq_gen", sequenceName = "FEE_COMMAND_SEQ", allocationSize = 1)
    private Long id;
    private String commandCode;
    private int totalRecord;
    private BigDecimal totalFee;
    private String createdUser;
    private LocalDateTime createdDate;
}

