package com.edabip.repository;

import com.edabip.dto.RoleResponse;
import com.edabip.entity.Role;
import com.edabip.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/** Runs against the H2 database created from schema.sql + data.sql (test profile). */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRoleRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;

    @Test
    void search_matchesNameOrEmailCaseInsensitively() {
        Page<User> page = userRepository.search("ALICE", null, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getEmail()).isEqualTo("alice@example.com");
        assertThat(page.getContent().get(0).getRole().getName()).isEqualTo("Administrator");
    }

    @Test
    void search_filtersByRoleAndPaginates() {
        Role viewer = roleRepository.findByNameIgnoreCase("Viewer").orElseThrow();

        Page<User> page = userRepository.search("", viewer.getId(),
                PageRequest.of(0, 2, Sort.by("name").ascending()));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isGreaterThan(2);
        assertThat(page.getContent()).allMatch(u -> u.getRole().getId().equals(viewer.getId()));
    }

    @Test
    void findAllWithUserCount_countsUsersPerRoleIncludingZero() {
        Page<RoleResponse> page = roleRepository.findAllWithUserCount("", PageRequest.of(0, 10));

        RoleResponse admin = page.getContent().stream().filter(r -> r.getName().equals("Administrator")).findFirst().orElseThrow();
        RoleResponse auditor = page.getContent().stream().filter(r -> r.getName().equals("Auditor")).findFirst().orElseThrow();
        assertThat(admin.getUserCount()).isEqualTo(1);
        assertThat(auditor.getUserCount()).isZero();
    }

    @Test
    void findAllWithPermissions_loadsPermissionsWithRoles() {
        var roles = roleRepository.findAllWithPermissions();

        Role admin = roles.stream().filter(r -> r.getName().equals("Administrator")).findFirst().orElseThrow();
        assertThat(admin.getPermissions()).hasSize(10);
    }
}
