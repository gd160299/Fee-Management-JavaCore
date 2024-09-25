package org.pj.Repository;

import org.pj.Entity.FeeTransaction;

import java.sql.SQLException;
import java.util.List;

public interface IFeeTransactionRepository {
    void insertFeeTransactions(List<FeeTransaction> transactions) throws SQLException;
    List<FeeTransaction> getTransactionsByCommandCode(String commandCode) throws SQLException;
    List<FeeTransaction> getTransactionsByStatusAndTotalScan(String status, int totalScan) throws SQLException;
    void updateFeeTransactions(List<FeeTransaction> transactions) throws SQLException;
}
