package com.edabip.repository;

import com.edabip.dto.RoleResponse;
import com.edabip.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // ---- derived query methods ----
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Optional<Role> findByNameIgnoreCase(String name);

    // ---- custom JPQL #2: roles with user count (one query, no per-role count) ----
    @Query(value = "select new com.edabip.dto.RoleResponse(r.id, r.name, r.description, r.status, count(u)) "
            + "from Role r left join User u on u.role = r "
            + "where lower(r.name) like lower(concat('%', :search, '%')) "
            + "group by r.id, r.name, r.description, r.status",
            countQuery = "select count(r) from Role r where lower(r.name) like lower(concat('%', :search, '%'))")
    Page<RoleResponse> findAllWithUserCount(@Param("search") String search, Pageable pageable);

    // ---- custom JPQL #3: permission matrix, roles + permissions in one query ----
    @Query("select distinct r from Role r left join fetch r.permissions order by r.id")
    List<Role> findAllWithPermissions();

    @Query("select r from Role r left join fetch r.permissions where r.id = :id")
    Optional<Role> findByIdWithPermissions(@Param("id") Long id);
}
