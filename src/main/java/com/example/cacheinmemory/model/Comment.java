package com.example.cacheinmemory.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class Comment {
  private int id;
  private String roomId;
  private String author;
  private String message;
  private LocalDateTime createAt = LocalDateTime.now();
  private LocalDateTime deletedAt;
  private boolean isEdited = false;
  private int parentId;
  private String userData;
}
