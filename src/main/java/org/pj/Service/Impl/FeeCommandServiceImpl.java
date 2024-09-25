package org.pj.Service.Impl;

import org.pj.Config.Redis.RedisConfig;
import org.pj.Dto.Request.FeeCommandDto;
import org.pj.Entity.FeeCommand;
import org.pj.Entity.FeeTransaction;
import org.pj.Enum.TransactionStatus;
import org.pj.Repository.IFeeCommandRepository;
import org.pj.Repository.IFeeTransactionRepository;
import org.pj.Service.IFeeCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeeCommandServiceImpl implements IFeeCommandService {
    private static final Logger logger = LoggerFactory.getLogger(FeeCommandServiceImpl.class);

    private final IFeeCommandRepository feeCommandRepository;
    private final IFeeTransactionRepository feeTransactionRepository;

    public FeeCommandServiceImpl(IFeeCommandRepository feeCommandRepository, IFeeTransactionRepository feeTransactionRepository) {
        this.feeCommandRepository = feeCommandRepository;
        this.feeTransactionRepository = feeTransactionRepository;
    }

    @Override
    public void addFeeCommand(FeeCommandDto objInput) throws Exception {
        logger.info("Begin addFeeCommand with requestId: {}", objInput.getRequestId());
        // validate requestId and requestTime
        validate(objInput);
        // Tạo mới FeeCommand
        FeeCommand feeCommand = new FeeCommand();
        feeCommand.setCommandCode(generateUniqueCommandCode());
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
            transaction.setId(generateUniqueId());
            transaction.setTransactionCode(generateUniqueTransactionCode());
            transaction.setCommandCode(objInput.getCommandCode());
            transaction.setFeeAmount(BigDecimal.ZERO); // gia lap du lieu
            transaction.setStatus(TransactionStatus.KHOI_TAO.getCode());
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
    }

    private Long generateUniqueId() {
        return System.currentTimeMillis() + (long)(Math.random() * 1000);
    }

    private String generateUniqueCommandCode() {
        return String.format("CMD%s", UUID.randomUUID());
    }

    private String generateUniqueTransactionCode() {
        return String.format("TRX%s", UUID.randomUUID());
    }

    private void validate(FeeCommandDto objInput) throws Exception {
        // Kiểm tra trùng requestId
        Jedis jedis = null;
        try {
            jedis = RedisConfig.getJedis();
            String requestIdKey = String.format("requestId:%s",objInput.getRequestId());
            if (jedis.exists(requestIdKey)) {
                throw new Exception("Duplicate requestId within a day.");
            }
        } finally {
            RedisConfig.close(jedis);
        }
        // Kiểm tra requestTime không quá 10 phút so với thời gian hiện tại
        LocalDateTime requestTime = LocalDateTime.parse(objInput.getRequestTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        if (Duration.between(requestTime, LocalDateTime.now()).abs().toMinutes() > 10) {
            throw new Exception("RequestTime is not within 10 minutes of current time.");
        }
    }
}
