# Deployment Guide

## 1. Create your environment file

Copy `.env.example` to `.env` and fill in the secret values:

- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `APP_CORS_ALLOWED_ORIGIN_PATTERNS`

The Aiven MySQL host, port, username, and database name are already prefilled from your screenshot.

## 2. Local Docker run

```bash
cp .env.example .env
docker compose up --build
```

Apps:

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`

## 3. Backend environment variables

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `SPRING_JPA_SHOW_SQL`
- `SPRING_JPA_DATABASE_PLATFORM`
- `SPRING_H2_CONSOLE_ENABLED`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `APP_CORS_ALLOWED_ORIGIN_PATTERNS`
- `SERVER_PORT`

## 4. Frontend environment variable

- `VITE_API_BASE_URL`

Use:

- `/api` when frontend and backend are deployed together behind the included nginx proxy
- `https://your-backend-domain.com` when frontend is deployed separately

## 5. Aiven note

Your current JDBC URL is configured for Aiven MySQL with SSL required and explicit TLS/timeouts:

```text
jdbc:mysql://bankapp-anurag-09f9.b.aivencloud.com:16194/defaultdb?sslMode=REQUIRED&enabledTLSProtocols=TLSv1.2&connectTimeout=10000&socketTimeout=30000&tcpKeepAlive=true
```

## 6. Important

Your screenshot does not reveal the real Aiven password, so that value still needs to be added manually to `.env`.

If your local machine still times out during the Aiven TLS handshake, the deployment config is still correct, but the network path between your machine and Aiven needs to be checked. In hosted environments this is often resolved automatically if outbound MySQL traffic is allowed.
