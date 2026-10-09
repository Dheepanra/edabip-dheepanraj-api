package com.edabip.users.repository;

import com.edabip.users.dto.UserStatsResponse;
import com.edabip.users.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Plain JDBC data access (JdbcTemplate) - no JPA / Hibernate. */
@Repository
public class UserRepository {

    private static final String COLUMNS = "id, user_id, name, email, role, status, last_login, created_at";

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUserId(rs.getString("user_id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        Timestamp lastLogin = rs.getTimestamp("last_login");
        u.setLastLogin(lastLogin == null ? null : lastLogin.toLocalDateTime());
        Timestamp createdAt = rs.getTimestamp("created_at");
        u.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        return u;
    };

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Builds the dynamic WHERE clause (search + role + status) and fills args. */
    private String buildWhere(String search, String role, String status, List<Object> args) {
        List<String> conditions = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            String like = "%" + search.trim().toLowerCase() + "%";
            conditions.add("(LOWER(user_id) LIKE ? OR LOWER(name) LIKE ? OR LOWER(email) LIKE ? OR LOWER(role) LIKE ?)");
            for (int i = 0; i < 4; i++) {
                args.add(like);
            }
        }
        if (role != null) {
            conditions.add("role = ?");
            args.add(role);
        }
        if (status != null) {
            conditions.add("status = ?");
            args.add(status);
        }
        return conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
    }

    /** sortColumn and direction are whitelisted by the service layer, so concatenation is safe. */
    public List<User> findAll(String search, String role, String status,
                              String sortColumn, String direction, int limit, int offset) {
        List<Object> args = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM users" + buildWhere(search, role, status, args)
                + " ORDER BY " + sortColumn + " " + direction + ", id ASC LIMIT ? OFFSET ?";
        args.add(limit);
        args.add(offset);
        return jdbc.query(sql, USER_MAPPER, args.toArray());
    }

    public long count(String search, String role, String status) {
        List<Object> args = new ArrayList<>();
        String sql = "SELECT COUNT(*) FROM users" + buildWhere(search, role, status, args);
        Long total = jdbc.queryForObject(sql, Long.class, args.toArray());
        return total == null ? 0 : total;
    }

    public Optional<User> findByUserId(String userId) {
        List<User> result = jdbc.query(
                "SELECT " + COLUMNS + " FROM users WHERE user_id = ?", USER_MAPPER, userId);
        return result.stream().findFirst();
    }

    public boolean emailExists(String email, String excludeUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = ?";
        Long count;
        if (excludeUserId == null) {
            count = jdbc.queryForObject(sql, Long.class, email.toLowerCase());
        } else {
            count = jdbc.queryForObject(sql + " AND user_id <> ?", Long.class, email.toLowerCase(), excludeUserId);
        }
        return count != null && count > 0;
    }

    /** Inserts the row and returns its generated numeric id. */
    public long insert(User u) {
        jdbc.update("INSERT INTO users (name, email, role, status, last_login) VALUES (?, ?, ?, ?, ?)",
                u.getName(), u.getEmail(), u.getRole(), u.getStatus(),
                u.getLastLogin() == null ? null : Timestamp.valueOf(u.getLastLogin()));
        // email is UNIQUE, so it is a portable way to read the generated id back (MySQL + H2)
        Long id = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, u.getEmail());
        return id == null ? -1 : id;
    }

    public void assignUserId(long id, String userId) {
        jdbc.update("UPDATE users SET user_id = ? WHERE id = ?", userId, id);
    }

    public int update(String userId, User u) {
        return jdbc.update("UPDATE users SET name = ?, email = ?, role = ?, status = ? WHERE user_id = ?",
                u.getName(), u.getEmail(), u.getRole(), u.getStatus(), userId);
    }

    public int deleteByUserId(String userId) {
        return jdbc.update("DELETE FROM users WHERE user_id = ?", userId);
    }

    public UserStatsResponse stats(LocalDateTime monthStart, LocalDateTime nextMonthStart) {
        String sql = "SELECT COUNT(*) AS total, "
                + "COALESCE(SUM(CASE WHEN status = 'Active' THEN 1 ELSE 0 END), 0) AS active, "
                + "COALESCE(SUM(CASE WHEN status = 'Inactive' THEN 1 ELSE 0 END), 0) AS inactive, "
                + "COALESCE(SUM(CASE WHEN created_at >= ? AND created_at < ? THEN 1 ELSE 0 END), 0) AS new_this_month "
                + "FROM users";
        return jdbc.queryForObject(sql,
                (rs, i) -> new UserStatsResponse(rs.getLong("total"), rs.getLong("active"),
                        rs.getLong("inactive"), rs.getLong("new_this_month")),
                Timestamp.valueOf(monthStart), Timestamp.valueOf(nextMonthStart));
    }
}
