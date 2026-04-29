package com.ksana.security;

import com.ksana.entity.User;
import com.ksana.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthzService {

    private final UserRepository userRepository;

    public AuthzService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isSelf(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // comes from UserDetails username

        User loggedIn = userRepository.findByUsername(username)
                .orElse(null);

        return loggedIn != null && loggedIn.getId().equals(userId);
    }

    public boolean isAdminOrSelf(Long userId) {
        return isAdmin() || isSelf(userId);
    }
}
