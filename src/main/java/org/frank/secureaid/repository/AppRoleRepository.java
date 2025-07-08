package org.frank.secureaid.repository;

import org.frank.secureaid.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Integer> {
    Optional<AppRole> findByRoleName(AppRole.RoleName roleName);
}
