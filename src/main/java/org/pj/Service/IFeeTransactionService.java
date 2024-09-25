package org.pj.Service;

import org.pj.Dto.Request.FeeCommandDto;

import java.sql.SQLException;

public interface IFeeTransactionService {
    void processFeeCommand(FeeCommandDto feeCommandDto) throws SQLException;
    void runCronJob() throws SQLException;
}
