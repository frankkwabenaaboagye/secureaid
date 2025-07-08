package org.frank.secureaid.appuser.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserResponse {

    private UUID id;
    private String username;
    private Set<String> roles; // Send role names (e.g., "ADMIN", "STAFF")
    private Instant createdAt;
    private Boolean isEnabled;
}
