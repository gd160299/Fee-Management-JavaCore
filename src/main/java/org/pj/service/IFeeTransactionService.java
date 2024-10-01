package org.pj.service;

import org.pj.dto.request.FeeCommandDto;

import java.sql.SQLException;

public interface IFeeTransactionService {
    void processFeeCommand(FeeCommandDto feeCommandDto) throws SQLException;
    void runCronJob() throws SQLException;
}
