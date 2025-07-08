## Generating Secret
```bash
openssl rand -base64 64
```

```bash
aws secretsmanager create-secret \
  --name theappname/profile \
  --secret-string '{"key-here":"value-here"}'
```
- response
```json
{                                                                                                                                                              
    "ARN": "arn:aws:secretsmanager:<region>:<account>:secret:s../d..",
    "Name": "s../d..",
    "VersionId": "4c.."
}


```

- exception Handling :: TODO

- Local Testing
```text
Before you can test these, you'll need to manually 
add at least one ADMIN role and one ADMIN user to your 
local PostgreSQL database, as the createAppUser endpoint 
requires an authenticated ADMIN user
```

```postgresql
INSERT INTO app_roles (role_name) VALUES ('ADMIN') ON CONFLICT (role_name) DO NOTHING;
INSERT INTO app_roles (role_name) VALUES ('STAFF') ON CONFLICT (role_name) DO NOTHING;
INSERT INTO app_roles (role_name) VALUES ('AUDITOR') ON CONFLICT (role_name) DO NOTHING;
```

```java

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin"; // Your desired admin password
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Hashed Password for '" + rawPassword + "': " + encodedPassword);
    }
}
```
- generate a UUID
```text
7dfdxxxx-049f-xxxx-856e-xxxx
```

```postgresql
-- Insert the admin user
INSERT INTO app_users (id, username, password_hash, created_at, is_enabled)
VALUES ('xxx-049f-xxx-x-19xxxf', 'admin', 'xxxxx.xxx', NOW(), TRUE)
ON CONFLICT (username) DO NOTHING;

-- Assign the ADMIN role to the new admin user
INSERT INTO app_user_roles (user_id, role_id)
SELECT (SELECT id FROM app_users WHERE username = 'admin'),
       (SELECT id FROM app_roles WHERE role_name = 'ADMIN')
ON CONFLICT (user_id, role_id) DO NOTHING;
```

- error response fix it