package com.example.cacheinmemory.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity(name = "user")
@Table(name = "driver_user")
@Data
@NoArgsConstructor
public class UserEntity {

    public UserEntity(Long id, String name, String imageUrl, String key, String location, String phone, List<LessonEntity> lessons, int lessonCount) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.key = key;
        this.location = location;
        this.phone = phone;
        this.lessons = lessons;
        this.lessonCount = lessonCount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200, unique = true)
    private String name;

    @Column(name = "imageUrl", length = 500)
    private String imageUrl;

    @Column(name = "secret_key", length = 20, nullable = false)
    private String key;

    @Column(name = "location", length = 150, nullable = false)
    private String location;
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @Fetch(FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<LessonEntity> lessons;

    @Column(name = "lesson_count", nullable = false)
    private int lessonCount;
}
