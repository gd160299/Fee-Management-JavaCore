package org.pj.Dto.Request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FeeCommandDto {
    private String requestId;
    private String requestTime;
    private String commandCode;
    private int totalRecord;
    private BigDecimal totalFee;
    private String createdUser;
    private String createdDate;
}
