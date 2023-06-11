package com.example.cacheinmemory.repository;

import com.example.cacheinmemory.entity.LessonEntity;
import org.springframework.data.repository.CrudRepository;

public interface LessonRepository extends CrudRepository<LessonEntity, Long> {
}
