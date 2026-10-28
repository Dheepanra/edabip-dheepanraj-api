package com.edabip.security;

import com.edabip.entity.Status;
import com.edabip.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Validates the Bearer token on every request. Created in SecurityConfig (not a @Component)
 * so it only runs inside the Spring Security filter chain.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String email = jwtService.parse(header.substring(7)).getSubject();
                // Re-read the user so deactivated users and role changes take effect immediately
                userRepository.findByEmailIgnoreCase(email)
                        .filter(u -> Status.ACTIVE.equals(u.getStatus()))
                        .ifPresent(u -> {
                            String authority = "ROLE_" + SecurityRoles.fromRoleName(u.getRole().getName());
                            var auth = new UsernamePasswordAuthenticationToken(
                                    u.getEmail(), null, List.of(new SimpleGrantedAuthority(authority)));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            } catch (JwtException | IllegalArgumentException ex) {
                // No authentication is set, so the entry point answers 401
                log.debug("Rejected JWT: {}", ex.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
