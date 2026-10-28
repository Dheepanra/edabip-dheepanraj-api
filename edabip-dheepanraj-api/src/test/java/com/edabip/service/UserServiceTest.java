package com.edabip.service;

import com.edabip.dto.UserRequest;
import com.edabip.dto.UserResponse;
import com.edabip.dto.UserStatsResponse;
import com.edabip.entity.Role;
import com.edabip.entity.Status;
import com.edabip.entity.User;
import com.edabip.exception.BadRequestException;
import com.edabip.exception.DuplicateResourceException;
import com.edabip.exception.ResourceNotFoundException;
import com.edabip.repository.RoleRepository;
import com.edabip.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    private Role role(long id, String name, String status) {
        Role r = new Role();
        r.setId(id);
        r.setName(name);
        r.setStatus(status);
        return r;
    }

    private UserRequest request(String email, Long roleId, String password) {
        UserRequest req = new UserRequest();
        req.setName("Priya Raman");
        req.setEmail(email);
        req.setRoleId(roleId);
        req.setPassword(password);
        return req;
    }

    private User existingUser() {
        User u = new User();
        u.setId(7L);
        u.setName("Old Name");
        u.setEmail("old@example.com");
        u.setRole(role(3, "Viewer", Status.ACTIVE));
        u.setStatus(Status.ACTIVE);
        u.setPasswordHash("hash");
        return u;
    }

    @Test
    void create_success_hashesPasswordAndSaves() {
        when(userRepository.existsByEmailIgnoreCase("priya@example.com")).thenReturn(false);
        when(roleRepository.findById(3L)).thenReturn(Optional.of(role(3, "Viewer", Status.ACTIVE)));
        when(passwordEncoder.encode("Password@123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(11L);
            return u;
        });

        UserResponse res = userService.create(request("priya@example.com", 3L, "Password@123"));

        assertThat(res.getId()).isEqualTo(11L);
        assertThat(res.getUserId()).isEqualTo("USR011");
        assertThat(res.getRole()).isEqualTo("Viewer");
        verify(userRepository).save(argThat(u -> "hashed".equals(u.getPasswordHash())));
    }

    @Test
    void create_duplicateEmail_throwsDuplicate() {
        when(userRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request("alice@example.com", 3L, "Password@123")))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("alice@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_missingPassword_throwsBadRequest() {
        assertThatThrownBy(() -> userService.create(request("new@example.com", 3L, null)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Password");
        verifyNoInteractions(roleRepository);
    }

    @Test
    void create_unknownRole_throwsNotFound() {
        when(userRepository.existsByEmailIgnoreCase(any())).thenReturn(false);
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(request("new@example.com", 99L, "Password@123")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_inactiveRole_throwsBadRequest() {
        when(userRepository.existsByEmailIgnoreCase(any())).thenReturn(false);
        when(roleRepository.findById(5L)).thenReturn(Optional.of(role(5, "Auditor", Status.INACTIVE)));

        assertThatThrownBy(() -> userService.create(request("new@example.com", 5L, "Password@123")))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    void get_notFound_throwsNotFound() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.get(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("404");
    }

    @Test
    void update_duplicateEmail_throwsDuplicate() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(existingUser()));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("taken@example.com", 7L)).thenReturn(true);

        assertThatThrownBy(() -> userService.update(7L, request("taken@example.com", 3L, null)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_changesRoleAndKeepsPasswordWhenNotSent() {
        User user = existingUser();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("new@example.com", 7L)).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role(2, "Editor", Status.ACTIVE)));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse res = userService.update(7L, request("new@example.com", 2L, null));

        assertThat(res.getRole()).isEqualTo("Editor");
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void delete_notFound_throwsNotFound() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(404L)).isInstanceOf(ResourceNotFoundException.class);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void stats_returnsAllFourCounts() {
        when(userRepository.count()).thenReturn(132L);
        when(userRepository.countByStatus(Status.ACTIVE)).thenReturn(98L);
        when(userRepository.countByStatus(Status.INACTIVE)).thenReturn(34L);
        when(userRepository.countByCreatedAtGreaterThanEqual(any(LocalDateTime.class))).thenReturn(15L);

        UserStatsResponse stats = userService.stats();

        assertThat(stats.getTotal()).isEqualTo(132);
        assertThat(stats.getActive()).isEqualTo(98);
        assertThat(stats.getInactive()).isEqualTo(34);
        assertThat(stats.getNewThisMonth()).isEqualTo(15);
    }
}
