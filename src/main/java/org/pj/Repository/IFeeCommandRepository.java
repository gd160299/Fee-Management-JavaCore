package org.pj.Repository;

import org.pj.Entity.FeeCommand;

import java.sql.SQLException;

public interface IFeeCommandRepository {
    void addFeeCommand(FeeCommand feeCommand) throws SQLException;
}
