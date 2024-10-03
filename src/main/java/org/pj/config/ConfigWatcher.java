package org.pj.config;

import lombok.Getter;
import org.aeonbits.owner.ConfigFactory;
import org.pj.config.db.DbConfig;
import org.pj.config.redis.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class ConfigWatcher {
    private final AppConfig appConfig;
    private static final Logger logger = LoggerFactory.getLogger(ConfigWatcher.class);
    private volatile boolean running = true;
    private final ExecutorService executor;
    private WatchService watchService;

    public ConfigWatcher() {
        this.appConfig = ConfigFactory.create(AppConfig.class);
        this.executor = Executors.newSingleThreadExecutor();
        watchConfigFile();
    }

    private void watchConfigFile() {
        executor.submit(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                Path configPath = Paths.get("./config");
                configPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (running) {
                    try {
                        WatchKey key = watchService.take();
                        if (key != null) {
                            processWatchEvents(key);
                            boolean valid = key.reset();
                            if (!valid) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.warn("Config watching interrupted", e);
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error("Error initializing watch service", e);
            } finally {
                closeWatchService();
            }
        });
    }

    private void processWatchEvents(WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                Path changed = (Path) event.context();
                if (changed.toString().endsWith("config.properties")) {
                    logger.info("Config file changed, reloading...");
                    reloadConfigurations();
                }
            }
        }
    }

    private void reloadConfigurations() {
        try {
            appConfig.reload();         // Tự động reload config khi file thay đổi
            DbConfig.reloadConfig();    // Reload Hikari pool
            RedisConfig.reloadConfig(); // Reload Redis pool
            logger.info("All configurations reloaded successfully");
        } catch (Exception e) {
            logger.error("Error reloading configurations", e);
        }
    }

    private void closeWatchService() {
        if (watchService != null) {
            try {
                watchService.close();
                running = false;
                executor.shutdownNow();
            } catch (IOException e) {
                logger.error("Error closing watch service", e);
            }
        }
    }
}
