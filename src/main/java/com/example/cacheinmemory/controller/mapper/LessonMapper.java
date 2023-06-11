package com.example.cacheinmemory.controller.mapper;

import com.example.cacheinmemory.entity.LessonEntity;
import com.example.cacheinmemory.model.LessonDto;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;

public interface LessonMapper {
    static LessonDto toDto(LessonEntity entity) {
        if (entity == null) {
            return null;
        }
        return new LessonDto(entity.getId(), entity.getName(), entity.getText(), entity.getUserId());
    }

    static LessonEntity toEntity(LessonDto dto) {
        if (dto == null) {
            return null;
        }
        return new LessonEntity(dto.id(), dto.name(), dto.text(), null, dto.userId());
    }

    static Collection<LessonDto> toDtos(Collection<LessonEntity> entities) {
        return CollectionUtils.isEmpty(entities) ? Collections.emptyList() : entities.stream().map(LessonMapper::toDto).toList();
    }

    default Collection<LessonEntity> toEntities(Collection<LessonDto> dtos) {
        return dtos.stream().map(LessonMapper::toEntity).toList();
    }
}
