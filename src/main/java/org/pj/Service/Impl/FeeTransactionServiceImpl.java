package org.pj.Service.Impl;

import org.pj.Dto.Request.FeeCommandDto;
import org.pj.Entity.FeeTransaction;
import org.pj.Enum.TransactionStatus;
import org.pj.Repository.IFeeTransactionRepository;
import org.pj.Service.IFeeTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


public class FeeTransactionServiceImpl implements IFeeTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(FeeTransactionServiceImpl.class);

    private final IFeeTransactionRepository feeTransactionRepository;

    public FeeTransactionServiceImpl(IFeeTransactionRepository feeTransactionRepository) {
        this.feeTransactionRepository = feeTransactionRepository;
    }

    @Override
    public void processFeeCommand(FeeCommandDto feeCommandDto) throws SQLException {
        logger.info("Begin processFeeCommand with commandCode: " + feeCommandDto.getCommandCode());

        // Lấy danh sách FeeTransaction theo commandCode
        List<FeeTransaction> transactions = feeTransactionRepository.getTransactionsByCommandCode(feeCommandDto.getCommandCode());
        logger.info("Retrieved transactions with count: " + transactions.size());

        // Cập nhật các giao dịch
        for (FeeTransaction transaction : transactions) {
            transaction.setTotalScan(1);
            transaction.setModifiedDate(LocalDateTime.now());
            transaction.setStatus(TransactionStatus.THU_PHI.getCode());
        }
        feeTransactionRepository.updateFeeTransactions(transactions);
        logger.info("Updated transactions with count: " + transactions.size());

        logger.info("End processFeeCommand for commandCode: " + feeCommandDto.getCommandCode());
    }

    @Override
    public void runCronJob() throws SQLException {
        logger.info("Begin cron job for updating FeeTransactions");

        List<FeeTransaction> transactions = feeTransactionRepository.getTransactionsByStatusAndTotalScan(TransactionStatus.THU_PHI.getCode(), 5);
        logger.info("Retrieved transactions for cron job with count: " + transactions.size());

        for (FeeTransaction transaction : transactions) {
            transaction.setTotalScan(transaction.getTotalScan() + 1);
            transaction.setModifiedDate(LocalDateTime.now());
            if (transaction.getTotalScan() >= 5) {
                transaction.setStatus(TransactionStatus.DUNG_THU.getCode());
            }
        }
        feeTransactionRepository.updateFeeTransactions(transactions);
        logger.info("Updated transactions in cron job with count: " + transactions.size());

        logger.info("End cron job for updating FeeTransactions");
    }
}
