package com.example.cacheinmemory.model;

import java.util.Collection;

public record UserDto(
        Long id,

        String name,

        String imageUrl,

        String key,

        String location,
        String phone,
        int lessonCount,
        Collection<LessonDto> lessons) {
}
