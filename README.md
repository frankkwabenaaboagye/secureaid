## Core Entity
- Donation 
- Donor (information embedded within Donation)
- Recipient (information embedded within Donation)
- AppUser
## Initial API Support
- Create Donation (Submit)
- Retrieve Single Donation (by ID)
- Retrieve List of Donations (with filters/pagination)

## Create Donation
- POST /api/donations
- request
```json
{
  "donorInfo": {
    "donorName": "String",      // REQUIRED. Sensitive, will be encrypted at rest.
    "donorContact": "String",   // REQUIRED. Sensitive, will be encrypted at rest.
    "donorAddress": "String",   // OPTIONAL. Sensitive, will be encrypted at rest.
    "isAnonymous": Boolean      // REQUIRED. If true, donor identity is masked in public views.
  },
  "donationDetails": {
    "type": "String",           // REQUIRED. Enum: "Cash", "Food", "Medical Supplies", "Shelter Materials", "Other".
    "amount": "Number",         // REQUIRED if type is "Cash".
    "currency": "String",       // REQUIRED if type is "Cash". Enum: "GHS", "USD", "EUR", etc.
    "quantity": "Number",       // REQUIRED if type is NOT "Cash".
    "unit": "String",           // REQUIRED if type is NOT "Cash". Enum: "KG", "Liters", "Units", "Packs", etc.
    "donationDate": "String",   // REQUIRED. ISO 8601 UTC (e.g., "YYYY-MM-DDTHH:mm:ssZ").
    "description": "String"     // OPTIONAL.
  },
  "recipientInfo": {
    "name": "String",           // REQUIRED.
    "location": "String",       // REQUIRED.
    "contactPerson": "String",  // OPTIONAL.
    "contactEmail": "String",   // OPTIONAL.
    "contactPhone": "String"    // OPTIONAL.
  },
  "notes": "String"             // OPTIONAL. Internal admin notes.
}
```
- response [201 - created]
```json
{
  "donationId": "String (UUID)",    // REQUIRED. Unique identifier for the new donation.
  "status": "CREATED",              // REQUIRED. Confirmation status.
  "message": "String",              // REQUIRED. User-friendly message.
  "recordedAt": "String (ISO 8601 UTC)", // REQUIRED. Timestamp when the system recorded it.
  "integrityHash": "String"         // REQUIRED. Cryptographic hash for digital integrity verification.
}
```

## Retrieve a Single Donation by ID
- GET /api/donations/{donationId}
- response body 201 - OK
```json
{
  "donationId": "String (UUID)",
  "donorInfo": {
    // Sensitive data is decrypted for *authorized* users.
    // Masked/omitted for unauthorized access (e.g., if isAnonymous=true or low privilege).
    "donorName": "String",
    "donorContact": "String",
    "donorAddress": "String",
    "isAnonymous": Boolean
  },
  "donationDetails": { /* ... (same as POST request body) ... */ },
  "recipientInfo": { /* ... (same as POST request body) ... */ },
  "notes": "String",                // Only for highly privileged users.
  "recordedAt": "String (ISO 8601 UTC)",
  "lastModifiedAt": "String (ISO 8601 UTC)", // OPTIONAL.
  "integrityHash": "String"
}
```

## Retrieve a List of Donations
- GET /api/donations
- Query Parameters (pagination, filtering, sorting)
- response body  200 OK
```json
{
  "content": [ // Array of simplified donation objects
    {
      "donationId": "String (UUID)",
      "donorInfo": { // Masked/summarized sensitive data for list views by default.
        "donorName": "String (Masked/Anonymous)",
        "isAnonymous": Boolean
        // donorContact, donorAddress omitted by default
      },
      "donationDetails": { /* ... subset of fields ... */ },
      "recipientInfo": { /* ... subset of fields ... */ },
      "recordedAt": "String (ISO 8601 UTC)",
      "integrityHash": "String"
    },
    // ... more simplified donation objects
  ],
  "pageable": { /* ... pagination metadata ... */ },
  "totalPages": "Number",
  "totalElements": "Number",
  "last": "Boolean",
  "first": "Boolean",
  "size": "Number",
  "number": "Number",
  "numberOfElements": "Number",
  "empty": "Boolean"
}
```

## AppUser
- primary goal for AppUser in the MVP is to enable authentication and authorization
- AppUser Authentication (Login)
  - POST /api/auth/login
    - To authenticate an AppUser and issue a JSON Web Token (JWT) for subsequent API requests.
```json
{
  "username": "String", // REQUIRED. The AppUser's unique username.
  "password": "String"  // REQUIRED. The AppUser's password.
}
```
- response body 200 OK
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlcyI6WyJBRE1JTiIsIkFVRElUT1IiXX0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c", // REQUIRED. The JWT for future requests.
  "tokenType": "Bearer",                                                                                                                                                                          // REQUIRED. Indicates the type of token.
  "expiresIn": 3600,                                                                                                                                                                              // REQUIRED. Integer. Token validity duration in seconds (e.g., 1 hour).
  "appUser": {                                                                                                                                                                                    // OPTIONAL. Basic AppUser info.
    "username": "adminUser",                                                                                                                                                                        // String.
    "roles": ["ADMIN", "AUDITOR"]                                                                                                                                                                   // Array of Strings. AppUser's roles for authorization.
  }
}
```

## AppUser Creation
- POST /api/appusers
```json
{
  "username": "newAppUser", // REQUIRED. String. The unique username for the new AppUser. Must be unique in the system.
  "password": "securePassword123!", // REQUIRED. String. The initial password for the new AppUser. Will be hashed before storage.
  "roles": ["STAFF"] // REQUIRED. Array of Strings. The roles assigned to this new AppUser (e.g., ["ADMIN"], ["STAFF"], ["AUDITOR"]).
}
```
- response
```json
{
  "appUserId": "uuid-of-new-user-1234", // REQUIRED. String (UUID). The unique identifier for the newly created AppUser.
  "username": "newAppUser",             // REQUIRED. String. The username of the created AppUser.
  "roles": ["STAFF"],                   // REQUIRED. Array of Strings. The roles assigned to the AppUser.
  "message": "AppUser 'newAppUser' created successfully.", // REQUIRED. String.
  "createdAt": "2025-07-08T13:15:00Z" // REQUIRED. String (ISO 8601 UTC). Timestamp of creation.
}
```

## Database Schema
-  PostgreSQL

## Tables
- app_users
- donations
- donor_info
- recipients
- app_user_roles

## AWS services and a high-level architecture for SecureAid's deployment
- Clients (Web/Mobile Apps) connect to api.secureaid.org via HTTPS. 
- Route 53 resolves api.secureaid.org to the Application Load Balancer (ALB). 
- The ALB distributes traffic to ECS Fargate Tasks (running Spring Boot API in Docker containers)
across multiple Availability Zones within a VPC.
- ECS Fargate Tasks connect to a PostgreSQL RDS instance 
(also within the VPC, ideally in private subnets with Multi-AZ for high availability).
- The Spring Boot application retrieves sensitive data 
encryption keys from AWS KMS and other credentials 
- (like database passwords, JWT secret) from AWS Secrets Manager.
- All application logs are streamed to CloudWatch Logs.
























