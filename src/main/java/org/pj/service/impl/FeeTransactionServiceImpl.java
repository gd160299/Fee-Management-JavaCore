package org.pj.service.impl;

import org.pj.dto.request.FeeCommandDto;
import org.pj.entity.FeeTransaction;
import org.pj.constant.EnumTransactionStatus;
import org.pj.repository.IFeeTransactionRepository;
import org.pj.repository.Impl.FeeTransactionRepositoryImpl;
import org.pj.service.IFeeTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;


public class FeeTransactionServiceImpl implements IFeeTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(FeeTransactionServiceImpl.class);

    private final IFeeTransactionRepository feeTransactionRepository = new FeeTransactionRepositoryImpl();

    public FeeTransactionServiceImpl() {
    }

    @Override
    public void processFeeCommand(FeeCommandDto feeCommandDto) throws SQLException {
        logger.info("Begin processFeeCommand with commandCode: {}",feeCommandDto.getCommandCode());

        List<FeeTransaction> transactions = feeTransactionRepository.getTransactionsByCommandCode(feeCommandDto.getCommandCode());
        logger.info("Retrieved transactions with count: {} ",transactions.size());

        for (FeeTransaction transaction : transactions) {
            transaction.setTotalScan(1);
            transaction.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            transaction.setStatus(EnumTransactionStatus.THU_PHI.getCode());
        }
        feeTransactionRepository.updateFeeTransactions(transactions);
        logger.info("Updated transactions with count: {}",transactions.size());

        logger.info("End processFeeCommand for commandCode: {}",feeCommandDto.getCommandCode());
    }

    @Override
    public void runCronJob() throws SQLException {
        logger.info("Begin cron job for updating FeeTransactions");

        List<FeeTransaction> transactions = feeTransactionRepository.getTransactionsByStatusAndTotalScan(EnumTransactionStatus.THU_PHI.getCode(), 5);
        logger.info("Retrieved transactions for cron job with count: {}",transactions.size());

        for (FeeTransaction transaction : transactions) {
            transaction.setTotalScan(transaction.getTotalScan() + 1);
            transaction.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            if (transaction.getTotalScan() >= 5) {
                transaction.setStatus(EnumTransactionStatus.DUNG_THU.getCode());
            }
        }
        feeTransactionRepository.updateFeeTransactions(transactions);
        logger.info("Updated transactions in cron job with count: {}",transactions.size());

        logger.info("End cron job for updating FeeTransactions");
    }
}
