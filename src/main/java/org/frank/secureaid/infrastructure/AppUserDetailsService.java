package org.frank.secureaid.infrastructure;

import lombok.RequiredArgsConstructor;
import org.frank.secureaid.model.AppUser;
import org.frank.secureaid.repository.AppUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("AppUser not found with username: " + username));

        // convert app user role to granted authority
        Collection<? extends GrantedAuthority> authorities = appUser
                .getRoles()
                .stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getRoleName().getSpringSecurityRole())
                )
                .toList();

        // return the spring security user object

        return new User(
                appUser.getUsername(),
                appUser.getPasswordHash(),
                appUser.getIsEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}
