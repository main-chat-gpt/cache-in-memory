package com.example.cacheinmemory.repository;

import com.example.cacheinmemory.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
