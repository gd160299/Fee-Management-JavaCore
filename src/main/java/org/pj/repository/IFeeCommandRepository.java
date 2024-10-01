package org.pj.repository;

import org.pj.entity.FeeCommand;

import java.sql.SQLException;

public interface IFeeCommandRepository {
    void addFeeCommand(FeeCommand feeCommand) throws SQLException;
}
