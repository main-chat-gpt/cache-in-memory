package com.example.cacheinmemory.controller;

import com.example.cacheinmemory.model.Comment;
import com.example.cacheinmemory.service.CommentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/cache")
@CrossOrigin(originPatterns = "${app.env.cors}")
public class CacheController {

  private final CommentService commentService;
  private static final AtomicInteger idGenerator = new AtomicInteger(0);

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
      comment.setId(comment.getId() != null ? comment.getId() : idGenerator.incrementAndGet());
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


  public void saveBackups() {
    cache.forEach((key, value) -> commentService.saveAllManually(value));
  }

  @PostConstruct
  public void restore() {
    final Collection<Comment> comments = commentService.getAllComments();
    if (CollectionUtils.isEmpty(comments)) {
      return;
    }
    comments.forEach(comment -> {
      if (idGenerator.get() < comment.getId()) {
        idGenerator.set(comment.getId());
      }
      cache.add(comment.getRoomId(), comment);
    });
  }

  @PatchMapping("/backup")
  public void doBackup(@RequestHeader String superUser) {
    if (!SUPERUSER.equals(superUser)) {
      return;
    }
    saveBackups();

  }
}
