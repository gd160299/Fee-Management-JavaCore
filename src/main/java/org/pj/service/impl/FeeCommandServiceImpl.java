package org.pj.service.impl;

import org.pj.config.db.DbConfig;
import org.pj.config.redis.RedisConfig;
import org.pj.dto.request.FeeCommandDto;
import org.pj.entity.FeeCommand;
import org.pj.entity.FeeTransaction;
import org.pj.constant.EnumTransactionStatus;
import org.pj.exception.BusinessException;
import org.pj.repository.IFeeCommandRepository;
import org.pj.repository.IFeeTransactionRepository;
import org.pj.repository.Impl.FeeCommandRepositoryImpl;
import org.pj.repository.Impl.FeeTransactionRepositoryImpl;
import org.pj.service.IFeeCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeeCommandServiceImpl implements IFeeCommandService {
    private static final Logger logger = LoggerFactory.getLogger(FeeCommandServiceImpl.class);

    private final IFeeCommandRepository feeCommandRepository = new FeeCommandRepositoryImpl();
    private final IFeeTransactionRepository feeTransactionRepository = new FeeTransactionRepositoryImpl();

    public FeeCommandServiceImpl() {
    }

    @Override
    public void addFeeCommand(FeeCommandDto objInput) throws Exception {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection(); // Mở connection từ ứng dụng chính
            connection.setAutoCommit(false);  // Tắt auto-commit để quản lý transaction
            logger.info("Begin addFeeCommand with requestId: {}", objInput.getRequestId());
            // validate requestId and requestTime
            validate(objInput);
            // Tạo mới FeeCommand
            FeeCommand feeCommand = new FeeCommand();
            String commandCode = generateUniqueCommandCode();
            feeCommand.setCommandCode(commandCode);
            feeCommand.setTotalRecord(objInput.getTotalRecord());
            feeCommand.setTotalFee(objInput.getTotalFee());
            feeCommand.setCreatedUser(objInput.getCreatedUser());
            feeCommand.setCreatedDate(LocalDateTime.now());

            feeCommandRepository.addFeeCommand(feeCommand);
            logger.info("FeeCommand saved with id: {}", feeCommand.getId());

            // Tạo danh sách FeeTransaction
            List<FeeTransaction> transactions = new ArrayList<>();
            for (int i = 0; i < objInput.getTotalRecord(); i++) {
                FeeTransaction transaction = new FeeTransaction();
                transaction.setTransactionCode(generateUniqueTransactionCode());
                transaction.setCommandCode(commandCode);
                transaction.setFeeAmount(BigDecimal.ZERO); // gia lap du lieu
                transaction.setStatus(EnumTransactionStatus.KHOI_TAO.getCode());
                transaction.setAccountNumber("admin_Giang");
                transaction.setTotalScan(0);
                transaction.setCreatedDate(LocalDateTime.now());
                transactions.add(transaction);
            }

            feeTransactionRepository.insertFeeTransactions(transactions);
            // Lưu requestId vào Redis với thời gian sống 1 ngày (86400 giây)
            Jedis jedis = RedisConfig.getJedis();
            String requestIdKey = String.format("requestId:%s",objInput.getRequestId());
            jedis.setex(requestIdKey, 86400, "1");
            logger.info("RequestId stored in Redis: {}", objInput.getRequestId());
            logger.info("FeeTransactions saved with count: {}", transactions.size());
            logger.info("End addFeeCommand for requestId: {}", objInput.getRequestId());

            connection.commit();
        }
        catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.error("Transaction rolled back due to an error", e);
                } catch (SQLException rollbackEx) {
                    logger.error("Error rolling back transaction", rollbackEx);
                }
            }
            throw e;
        }
    }

    private String generateUniqueCommandCode() {
        return String.format("CMD%s", UUID.randomUUID());
    }

    private String generateUniqueTransactionCode() {
        return String.format("TRX%s", UUID.randomUUID());
    }

    private void validate(FeeCommandDto objInput) {
        // Kiểm tra trùng requestId
        Jedis jedis = null;
        try {
            jedis = RedisConfig.getJedis();
            String requestIdKey = String.format("requestId:%s",objInput.getRequestId());
            if (jedis.exists(requestIdKey)) {
                throw new BusinessException(409,"Duplicate requestId within a day.");
            }
        } finally {
            RedisConfig.close(jedis);
        }
        // Kiểm tra requestTime không quá 10 phút so với thời gian hiện tại
        LocalDateTime requestTime = LocalDateTime.parse(objInput.getRequestTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        if (Duration.between(requestTime, LocalDateTime.now()).abs().toMinutes() > 10) {
            throw new BusinessException(500, "RequestTime is not within 10 minutes of current time.");
        }
    }
}
