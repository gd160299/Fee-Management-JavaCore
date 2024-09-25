package org.pj.Repository.Impl;

import oracle.jdbc.OracleTypes;
import org.pj.Config.Db.DbConfig;
import org.pj.Entity.FeeTransaction;
import org.pj.Repository.IFeeTransactionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeeTransactionRepositoryImpl implements IFeeTransactionRepository {

    @Override
    public void insertFeeTransactions(List<FeeTransaction> transactions) throws SQLException {
        String sql = "{call PKG_FEE_MANAGEMENT.INSERT_FEE_TRANS(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection connection = DbConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(sql)) {

            for (FeeTransaction transaction : transactions) {
                callableStatement.setString(1, transaction.getTransactionCode());
                callableStatement.setString(2, transaction.getCommandCode());
                callableStatement.setBigDecimal(3, transaction.getFeeAmount());
                callableStatement.setString(4, transaction.getStatus());
                callableStatement.setString(5, transaction.getAccountNumber());
                callableStatement.setInt(6, transaction.getTotalScan());
                callableStatement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                callableStatement.addBatch();
            }
            callableStatement.executeBatch();
        }
    }

    @Override
    public List<FeeTransaction> getTransactionsByCommandCode(String commandCode) throws SQLException {
        List<FeeTransaction> transactions = new ArrayList<>();
        String sql = "{call PKG_FEE_MANAGEMENT.GET_FEE_TRANS_BY_COMMAND_CODE(?, ?)}";
        try (Connection connection = DbConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(sql)) {

            callableStatement.setString(1, commandCode);
            callableStatement.registerOutParameter(2, OracleTypes.CURSOR);

            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(2);
            while (resultSet.next()) {
                FeeTransaction transaction = mapResultSetToFeeTransaction(resultSet);
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public List<FeeTransaction> getTransactionsByStatusAndTotalScan(String status, int totalScan) throws SQLException {
        List<FeeTransaction> transactions = new ArrayList<>();
        String sql = "{call PKG_FEE_MANAGEMENT.GET_FEE_TRANS_BY_STATUS_SCAN(?, ?, ?)}";
        try (Connection connection = DbConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(sql)) {

            callableStatement.setString(1, status);
            callableStatement.setInt(2, totalScan);
            callableStatement.registerOutParameter(3, OracleTypes.CURSOR);

            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(3);
            while (resultSet.next()) {
                FeeTransaction transaction = mapResultSetToFeeTransaction(resultSet);
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public void updateFeeTransactions(List<FeeTransaction> transactions) throws SQLException {
        String sql = "{call PKG_FEE_MANAGEMENT.UPDATE_FEE_TRANS(?, ?, ?, ?)}";
        try (Connection connection = DbConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(sql)) {

            connection.setAutoCommit(false); // Quản lý transaction từ Java

            for (FeeTransaction transaction : transactions) {
                callableStatement.setLong(1, transaction.getId());
                callableStatement.setInt(2, transaction.getTotalScan());
                callableStatement.setString(3, transaction.getStatus());
                callableStatement.setTimestamp(4, java.sql.Timestamp.valueOf(transaction.getModifiedDate()));
                callableStatement.addBatch();
            }
            callableStatement.executeBatch();

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private FeeTransaction mapResultSetToFeeTransaction(ResultSet resultSet) throws SQLException {
        FeeTransaction transaction = new FeeTransaction();
        transaction.setId(resultSet.getLong("ID"));
        transaction.setTransactionCode(resultSet.getString("TRANSACTION_CODE"));
        transaction.setCommandCode(resultSet.getString("COMMAND_CODE"));
        transaction.setFeeAmount(resultSet.getBigDecimal("FEE_AMOUNT"));
        transaction.setStatus(resultSet.getString("STATUS"));
        transaction.setAccountNumber(resultSet.getString("ACCOUNT_NUMBER"));
        transaction.setTotalScan(resultSet.getInt("TOTAL_SCAN"));
        transaction.setCreatedDate(resultSet.getTimestamp("CREATED_DATE").toLocalDateTime());
        transaction.setModifiedDate(resultSet.getTimestamp("MODIFIED_DATE") != null ? resultSet.getTimestamp("MODIFIED_DATE").toLocalDateTime() : null);
        return transaction;
    }
}