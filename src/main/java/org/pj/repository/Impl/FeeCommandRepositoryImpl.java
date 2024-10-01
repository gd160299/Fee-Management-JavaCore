package org.pj.repository.Impl;

import org.pj.config.db.DbConfig;
import org.pj.entity.FeeCommand;
import org.pj.repository.IFeeCommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FeeCommandRepositoryImpl implements IFeeCommandRepository {

    private static final Logger logger = LoggerFactory.getLogger(FeeCommandRepositoryImpl.class);

    @Override
    public void addFeeCommand(FeeCommand feeCommand) throws SQLException {
        logger.info("Begin: addFeeCommand with CommandCode: {}", feeCommand.getCommandCode());
        String sql = "{call PKG_FEE_MANAGEMENT.INSERT_FEE_COMMAND(?, ?, ?, ?, ?)}";
        try (Connection connection = DbConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setString(1, feeCommand.getCommandCode());
            callableStatement.setInt(2, feeCommand.getTotalRecord());
            callableStatement.setBigDecimal(3, feeCommand.getTotalFee());
            callableStatement.setString(4, feeCommand.getCreatedUser());
            callableStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            callableStatement.execute();
            logger.info("End: addFeeCommand for CommandCode: {}", feeCommand.getCommandCode());
        } catch (SQLException e) {
            logger.error("Error executing addFeeCommand for CommandCode: {}", feeCommand.getCommandCode(), e);
            throw e;
        }
    }
}
