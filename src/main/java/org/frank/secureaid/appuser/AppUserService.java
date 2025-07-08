package org.frank.secureaid.appuser;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.frank.secureaid.model.AppRole;
import org.frank.secureaid.model.AppUser;
import org.frank.secureaid.repository.AppRoleRepository;
import org.frank.secureaid.repository.AppUserRepository;
import org.frank.secureaid.appuser.payload.AppUserRequest;
import org.frank.secureaid.appuser.payload.AppUserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppUserResponse createAppUser(AppUserRequest request) {

        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        AppRole defaultRole = appRoleRepository.findByRoleName(AppRole.RoleName.STAFF) //TODO :: to modify this
                .orElseThrow(() -> new IllegalStateException("Default STAFF role not found. Please ensure roles are initialized."));

        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        appUser.setIsEnabled(true); // new users are enabled by default

        // assign roles based on the request, or default to STAFF
        if(request.getRoles() != null && !request.getRoles().isEmpty()){
            Set<AppRole> assignedRoles = new HashSet<>();
            for(String roleName : request.getRoles()){
                AppRole.RoleName enumRole;
                try{
                    enumRole = AppRole.RoleName.valueOf(roleName.toUpperCase());
                }catch(IllegalArgumentException e){
                    throw new IllegalArgumentException("Invalid role specified: " + roleName);
                }
                AppRole role = appRoleRepository.findByRoleName(enumRole)
                        .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));
                assignedRoles.add(role);
            }
            appUser.setRoles(assignedRoles);
        }else{
            // Assign default STAFF role if no roles are specified
            appUser.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));
        }

        AppUser savedUser = appUserRepository.save(appUser);

        return new AppUserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRoles().stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toSet()),
                savedUser.getCreatedAt(),
                savedUser.getIsEnabled()
        );

    }

    // find user by ID (useful for security context or internal operations)
    public Optional<AppUser> findUserById(UUID userId) {
        return appUserRepository.findById(userId);
    }

    // find user by username (useful for security context or internal operations)
    public Optional<AppUser> findUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }
}
