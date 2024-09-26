package org.pj;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.pj.Api.FeeCommandHandler;
import org.pj.Api.FeeCommandProcessingHandler;
import org.pj.Exception.ExceptionHandler;
import org.pj.Service.Impl.FeeTransactionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        new Thread(Main::startNettyServer).start();
        // Chạy cron job
        runJob();
    }

    private static void runJob() {
        FeeTransactionServiceImpl feeTransactionService = new FeeTransactionServiceImpl();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            try {
                logger.info("Cron job started");
                feeTransactionService.runCronJob();
                logger.info("Cron job finished");
            } catch (Exception e) {
                logger.error("Error in cron job", e);
            }
        };

        // Lịch chạy mỗi 3 phút
        scheduler.scheduleAtFixedRate(task, 0, 3, TimeUnit.MINUTES);
    }

    private static void startNettyServer() {
        // Khởi chạy Netty Server
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            ch.pipeline().addLast(new FeeCommandHandler());
                            ch.pipeline().addLast(new FeeCommandProcessingHandler());
                            ch.pipeline().addLast(new ExceptionHandler());
                        }
                    });

            ChannelFuture f = b.bind(8082).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}