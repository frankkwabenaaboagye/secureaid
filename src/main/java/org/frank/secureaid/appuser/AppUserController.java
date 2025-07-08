package org.frank.secureaid.appuser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.frank.secureaid.appuser.payload.AppUserRequest;
import org.frank.secureaid.appuser.payload.AppUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appusers")
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can create new AppUsers via this endpoint
    public ResponseEntity<AppUserResponse> createUser(@Valid @RequestBody AppUserRequest request){
        AppUserResponse response = appUserService.createAppUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // TODO: add more AppUser-related endpoints here later (e.g., get user profile, update user)
    // For MVP, AppUser creation by ADMIN and login are the core functionalities.
}
