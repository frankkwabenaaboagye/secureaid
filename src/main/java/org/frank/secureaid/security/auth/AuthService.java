/**
 * This service will handle the login process and delegate JWT generation.
 */

package org.frank.secureaid.security.auth;

import lombok.RequiredArgsConstructor;
import org.frank.secureaid.model.AppUser;
import org.frank.secureaid.repository.AppUserRepository;
import org.frank.secureaid.security.jwt.JwtService;
import org.frank.secureaid.security.payload.LoginRequest;
import org.frank.secureaid.security.payload.LoginResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthentionManager authentionManager; // TODO :: add this bean to WebSecurityConfig
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

        // authenticate user and return jwt
    public LoginResponse authenticate(LoginRequest request){
        Authentication authentication = authentionManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // If authentication is successful, update last login time and generate token
        Optional<AppUser> optionalAppUser = appUserRepository.findByUsername(request.getUsername());
        if(optionalAppUser.isPresent()){

            AppUser appUser = optionalAppUser.get();
            appUser.setLastLoginAt(Instant.now());
            appUserRepository.save(appUser);

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles",
                    appUser.getRoles()
                            .stream()
                            .map(role -> role.getRoleName().name())
                            .collect(Collectors.toList())

            );
            claims.put("userId", appUser.getId().toString());

            String jwt = jwtService.generateToken(appUser.getUsername(), claims);

            return new LoginResponse(jwt);


        }
        throw new UsernameNotFoundException("User not found after authentication success (should not happen)")
    }


}
