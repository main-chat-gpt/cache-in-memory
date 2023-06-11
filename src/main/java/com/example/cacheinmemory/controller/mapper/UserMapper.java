package com.example.cacheinmemory.controller.mapper;

import com.example.cacheinmemory.entity.UserEntity;
import com.example.cacheinmemory.model.UserDto;

import java.util.Objects;

public interface UserMapper {

    static UserDto toDto(UserEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return new UserDto(
                entity.getId(),
                entity.getName(),
                entity.getImageUrl(),
                entity.getKey(),
                entity.getLocation(),
                entity.getPhone(),
                entity.getLessonCount(),
                LessonMapper.toDtos(entity.getLessons())
        );
    }

    static UserEntity toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return new UserEntity(
                dto.id(),
                dto.name(),
                dto.imageUrl(),
                dto.key(),
                dto.location(),
                dto.phone(),
                null,
                dto.lessonCount());
    }
}
