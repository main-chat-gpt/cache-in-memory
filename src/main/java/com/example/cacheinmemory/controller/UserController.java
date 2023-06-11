package com.example.cacheinmemory.controller;

import com.example.cacheinmemory.controller.mapper.UserMapper;
import com.example.cacheinmemory.model.UserDto;
import com.example.cacheinmemory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/user")
@CrossOrigin(originPatterns = "${app.env.cors}")
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    UserDto createUser(@RequestBody UserDto user) {
        return UserMapper.toDto(userRepository.save(UserMapper.toEntity(user)));
    }

    @GetMapping("/all")
    Collection<UserDto> getAllUsers() {
        final List<UserDto> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> users.add(UserMapper.toDto(user)));
        return users;
    }

}
