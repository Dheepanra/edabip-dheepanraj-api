package com.edabip.repository;

import com.edabip.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ---- derived query methods ----
    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByStatus(String status);

    long countByCreatedAtGreaterThanEqual(LocalDateTime from);

    long countByRoleId(Long roleId);

    List<User> findByPasswordHash(String passwordHash);

    @Override
    @EntityGraph(attributePaths = "role")
    Optional<User> findById(Long id);

    // ---- custom JPQL #1: search + role filter ----
    // @EntityGraph fetches the role in the same SQL (join), so listing N users
    // does not trigger N extra "select role" queries (N+1 problem).
    @EntityGraph(attributePaths = "role")
    @Query(value = "select u from User u "
            + "where (lower(u.name) like lower(concat('%', :search, '%')) "
            + "       or lower(u.email) like lower(concat('%', :search, '%'))) "
            + "and (:roleId is null or u.role.id = :roleId)",
            countQuery = "select count(u) from User u "
                    + "where (lower(u.name) like lower(concat('%', :search, '%')) "
                    + "       or lower(u.email) like lower(concat('%', :search, '%'))) "
                    + "and (:roleId is null or u.role.id = :roleId)")
    Page<User> search(@Param("search") String search, @Param("roleId") Long roleId, Pageable pageable);
}
