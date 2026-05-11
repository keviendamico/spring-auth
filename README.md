# Spring Auth

JWT authentication service built with Spring Boot 4, MySQL, and Redis.

**Features:** register, login, token refresh (with rotation), logout (blacklist via Redis), protected endpoints.

---

## Setup

### 1. Environment variables

```bash
export JWT_SECRET=<your-256-bit-base64-encoded-secret>
```

To generate a secure secret:
```bash
openssl rand -base64 32
```

Optionally override the Docker database credentials:
```bash
export DB_NAME=mydatabase
export DB_PASSWORD=secret
export DB_ROOT_PASSWORD=verysecret
export DB_USER=myuser
```

### 2. Start infrastructure

```bash
docker-compose up -d
```

MySQL runs the schema from `db/schema.sql` automatically on first start.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

---

## API

### Auth — `/api/v1/auth` (public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | Login, returns access + refresh token |
| POST | `/api/v1/auth/refresh` | Exchange refresh token for a new token pair |
| POST | `/api/v1/auth/logout` | Revoke access + refresh token |

### Users — `/api/v1/users`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/users` | — | Register a new user |
| GET | `/api/v1/users/me` | Bearer | Get current authenticated user |

---

## Testing

Open `endpoints/user.http` and run **CREATE USER**, then open `endpoints/auth.http` and run the requests in order:

1. **LOGIN** — returns `jwt` and `refreshToken`
2. Copy both values into `endpoints/http-client.env.json`
3. Open `endpoints/user.http` and run **ME** → returns the user profile

### Testing logout

1. Run **LOGOUT** in `endpoints/auth.http`
2. Run **ME** again → expected: `401 Unauthorized`

### Testing refresh

1. Run **REFRESH** with a valid `refreshToken`
2. Refresh tokens are rotated on use — the old one is immediately revoked