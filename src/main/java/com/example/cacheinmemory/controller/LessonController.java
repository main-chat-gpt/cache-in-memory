package com.example.cacheinmemory.controller;

import com.example.cacheinmemory.controller.mapper.LessonMapper;
import com.example.cacheinmemory.entity.LessonEntity;
import com.example.cacheinmemory.model.LessonDto;
import com.example.cacheinmemory.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/lesson")
@CrossOrigin(originPatterns = "${app.env.cors}")
public class LessonController {

    private final LessonRepository lessonRepository;

    @PostMapping
    LessonDto createLesson(@RequestBody LessonDto lesson) {
        return LessonMapper.toDto(lessonRepository.save(LessonMapper.toEntity(lesson)));
    }

    @PostMapping("/all")
    Collection<LessonDto> createLesson(@RequestBody Collection<LessonDto> lessons) {
        return LessonMapper.toDtos(
                lessons.stream().map(lesson -> lessonRepository.save(LessonMapper.toEntity(lesson)))
                        .toList()
        );
    }

    @GetMapping("/all")
    Collection<LessonDto> getAllLessons() {
        return LessonMapper.toDtos((Collection<LessonEntity>) lessonRepository.findAll());
    }

}
