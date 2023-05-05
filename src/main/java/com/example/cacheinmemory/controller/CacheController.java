package com.example.cacheinmemory.controller;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping(path = "/api/cache")
public class CacheController {

  private static final MultiValueMap<String, Comment> cache = new LinkedMultiValueMap<>();
  private final Lock lock = new ReentrantLock();

  record Comment(int id, String roomId, String author, String message, LocalDateTime createAt, LocalDateTime deletedAt,
                 boolean isEdited, int parentId, String userData) {
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<Comment> getCommentByRoom(@RequestParam String roomId) {
    return cache.get(roomId);
  }

  @PostMapping
  public Collection<Comment> postCommentToRoom(@RequestBody Comment comment, @RequestParam String roomId) {
    try {
      lock.lock();
      cache.add(roomId, comment);
    } finally {
      lock.unlock();
    }
    return cache.get(roomId);
  }
}
