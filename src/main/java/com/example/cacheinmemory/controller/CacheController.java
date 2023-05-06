package com.example.cacheinmemory.controller;

import com.example.cacheinmemory.model.Comment;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping(path = "/api/cache")
public class CacheController {

  private static final String SUPERUSER = "pthyzpthyz";

  private static final MultiValueMap<String, Comment> cache = new LinkedMultiValueMap<>();
  private final Lock lock = new ReentrantLock();

  @PostMapping("/restore/{roomId}")
  public void restore(@RequestHeader String superUser, @PathVariable String roomId, @RequestBody List<Comment> comments) {
    if (!SUPERUSER.equals(superUser)) {
      return;
    }
    if (CollectionUtils.isEmpty(comments)) {
      return;
    }
    try {
      lock.lock();
      cache.remove(roomId);
      cache.put(roomId, comments);
    } finally {
      lock.unlock();
    }
  }

  private record Pair(String roomId, Collection<Comment> comments) {
  }

  @GetMapping("/backup")
  public Collection<Pair> getBackup(@RequestHeader String superUser) {
    if (!SUPERUSER.equals(superUser)) {
      return Collections.emptyList();
    }
    return cache.entrySet()
        .stream()
        .map(item -> new Pair(item.getKey(), item.getValue())).toList();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<Comment> getCommentByRoom(@RequestParam String roomId) {
    return cache.getOrDefault(roomId, Collections.emptyList())
        .stream()
        .sorted(Comparator.comparing(Comment::getCreateAt))
        .toList();
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

  @DeleteMapping("/{id}")
  public void deleteComment(@RequestBody DeleteCommentRequest request, @PathVariable int id) {
    if (!SUPERUSER.equals(request.superuser)) {
      return;
    }
    val comments = cache.get(request.roomId);
    if (CollectionUtils.isEmpty(comments)) {
      return;
    }
    try {
      lock.lock();
      comments.stream()
          .filter(item -> item.getId() == id)
          .findFirst().ifPresent(item -> item.setDeletedAt(LocalDateTime.now()));
    } finally {
      lock.unlock();
    }
  }

  private record DeleteCommentRequest(String roomId, String superuser) {
  }

  @DeleteMapping()
  public void deleteAllComments(@RequestBody DeleteCommentRequest request) {
    if (!SUPERUSER.equals(request.superuser)) {
      return;
    }
    val comments = cache.get(request.roomId);
    if (CollectionUtils.isEmpty(comments)) {
      return;
    }
    try {
      lock.lock();
      comments.forEach(item -> item.setDeletedAt(LocalDateTime.now()));
    } finally {
      lock.unlock();
    }
  }
}
