package com.example.cacheinmemory.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class Comment {
  private Integer id;
  private String roomId;
  private String author;
  private String message;
  private LocalDateTime createAt = LocalDateTime.now();
  private LocalDateTime deletedAt;
  private boolean isEdited = false;
  private int parentId;
  private String userData;

  public static Comment of(int id, String roomId, String author, String message, Timestamp createAt, Timestamp deletedAt, boolean isEdited, int parentId, String userData) {
    val comment = new Comment();
    comment.id = id;
    comment.roomId = roomId;
    comment.author = author;
    comment.message = message;
    comment.createAt = createAt.toLocalDateTime();
    comment.deletedAt = deletedAt == null ? null : deletedAt.toLocalDateTime();
    comment.isEdited = isEdited;
    comment.parentId = parentId;
    comment.userData = userData;
    return comment;
  }

}
