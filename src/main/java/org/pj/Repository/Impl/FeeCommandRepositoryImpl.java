package org.pj.Repository.Impl;

import org.pj.Config.Db.DbConfig;
import org.pj.Entity.FeeCommand;
import org.pj.Repository.IFeeCommandRepository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class FeeCommandRepositoryImpl implements IFeeCommandRepository {
    @Override
    public void addFeeCommand(FeeCommand feeCommand) throws SQLException {
        String sql = "{call PKG_FEE_MANAGEMENT.INSERT_FEE_COMMAND(?, ?, ?, ?, ?)}";
        try (Connection connection = DbConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setString(1, feeCommand.getCommandCode());
            callableStatement.setInt(2, feeCommand.getTotalRecord());
            callableStatement.setBigDecimal(3, feeCommand.getTotalFee());
            callableStatement.setString(4, feeCommand.getCreatedUser());
            callableStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            callableStatement.execute();
        }
    }
}
