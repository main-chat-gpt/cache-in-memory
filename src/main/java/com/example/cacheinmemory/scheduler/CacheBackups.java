package com.example.cacheinmemory.scheduler;

import com.example.cacheinmemory.controller.CacheController;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheBackups {

  private final CacheController cacheController;

  @Scheduled(cron = "${app.backup-period}")
  public void backups() {
    cacheController.saveBackups();
  }
}
