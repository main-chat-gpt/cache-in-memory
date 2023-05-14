package com.example.cacheinmemory.service;

import com.example.cacheinmemory.model.Comment;
import com.zaxxer.hikari.HikariConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final JdbcTemplate jdbcTemplate;

    private final HikariConfig hikariConfig;

    public boolean saveAllManually(final Collection<Comment> comments) {
        if (CollectionUtils.isEmpty(comments)) {
            return false;
        }
        try (Connection con = DriverManager.getConnection(hikariConfig.getJdbcUrl(), hikariConfig.getUsername(), hikariConfig.getPassword())) {
            final Savepoint beginBackup = con.setSavepoint("begin backup");
            try (PreparedStatement stmt = con.prepareStatement("delete from comments where 1=1")) {
                stmt.execute();
                save(comments, con, beginBackup);
            } catch (SQLException sqlException) {
                log.error("Error while executing the sql command , message {}", sqlException.getLocalizedMessage());
                con.rollback(beginBackup);
                return false;
            }
        } catch (Exception e) {
            log.error("Error while connecting to Database, message {}", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private static void save(Collection<Comment> comments, Connection con, Savepoint beginBackup) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO comments (id, author, created_at, message, parent_id, is_edited, deleted_date, user_data, room_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            comments.stream().map(comment -> {
                try {
                    ps.setInt(1, comment.getId());
                    ps.setString(2, comment.getAuthor());
                    ps.setTimestamp(3, Timestamp.valueOf(comment.getCreateAt()));
                    ps.setString(4, comment.getMessage());
                    ps.setInt(5, comment.getParentId());
                    ps.setBoolean(6, comment.isEdited());
                    if (Objects.nonNull(comment.getDeletedAt())) {
                        ps.setTimestamp(7, Timestamp.valueOf(comment.getDeletedAt()));
                    } else {
                        ps.setTimestamp(7, null);
                    }
                    ps.setString(8, comment.getUserData());
                    ps.setString(9, comment.getRoomId());
                    ps.execute();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                    return false;
                }
                return true;
            }).filter(item -> !item).findFirst().ifPresent(item -> {
                try {
                    con.rollback(beginBackup);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Transactional
    public void saveAll(final Collection<Comment> comments) {
        if (CollectionUtils.isEmpty(comments)) {
            return;
        }
        jdbcTemplate.update("delete from comments where 1=1");
        jdbcTemplate.batchUpdate("INSERT INTO comments (id, author, created_at, message, parent_id, is_edited, deleted_date, user_data, room_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                comments,
                100,
                (PreparedStatement ps, Comment comment) -> {
                    ps.setInt(1, comment.getId());
                    ps.setString(2, comment.getAuthor());
                    ps.setTimestamp(3, Timestamp.valueOf(comment.getCreateAt()));
                    ps.setString(4, comment.getMessage());
                    ps.setInt(5, comment.getParentId());
                    ps.setBoolean(6, comment.isEdited());
                    if (Objects.nonNull(comment.getDeletedAt())) {
                        ps.setTimestamp(7, Timestamp.valueOf(comment.getDeletedAt()));
                    } else {
                        ps.setTimestamp(7, null);
                    }
                    ps.setString(8, comment.getUserData());
                    ps.setString(9, comment.getRoomId());
                });
    }

    public Collection<Comment> getAllComments() {
        return jdbcTemplate.query("select id, author, created_at, message, parent_id, is_edited, deleted_date, user_data, room_id "
                        + " from comments order by created_at",
                (rs, rowNum) -> Comment.of(
                        rs.getInt("id"),
                        rs.getString("room_id"),
                        rs.getString("author"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("deleted_date"),
                        rs.getBoolean("is_edited"),
                        rs.getInt("parent_id"),
                        rs.getString("user_data")
                ));
    }
}
