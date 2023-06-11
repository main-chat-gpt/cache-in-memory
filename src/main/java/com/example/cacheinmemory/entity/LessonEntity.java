package com.example.cacheinmemory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "lesson")
@Table(name = "lesson")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = false)
    private String name;

    @Column(name = "text", length = 500)
    private String text;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "user_id")
    private Long userId;
}
