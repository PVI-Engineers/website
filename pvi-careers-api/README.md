# PVI Careers API

Spring Boot backend for PVI ENGINEERS careers applications.

## Features

- Two-step job application flow:
  - `POST /api/careers/applications/presign` for S3 pre-signed upload URLs
  - Direct browser upload to S3 (resume + supporting documents)
  - `POST /api/careers/applications` JSON submit with uploaded file metadata
- Role-based login (Admin, HR, Employee) with JWT
- Endpoint-aware IP rate limiting for login and public application endpoints
- Security headers on API responses (HSTS, frame deny, strict referrer policy)
- HR/Admin file download endpoints
- Resume + multiple supporting file support (max 4 files, 9 MB total)
- Storage provider for application files:
  - `s3` (required for pre-signed uploads)
- Pluggable mail provider:
  - `smtp` (Gmail/SMTP)
  - `ses` (AWS SES via SDK)

## Tech Stack

- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- AWS SDK v2 (S3, SES)

## Environment Variables

See `.env.example` for full list.

Core:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`
- `FRONTEND_URLS`
- `APP_RATE_LIMIT_ENABLED`
- `APP_RATE_LIMIT_LOGIN_PER_MINUTE`
- `APP_RATE_LIMIT_PRESIGN_PER_MINUTE`
- `APP_RATE_LIMIT_APPLICATION_SUBMIT_PER_MINUTE`
- `APP_RATE_LIMIT_HEALTH_PER_MINUTE`

Mail:

- `APP_MAIL_PROVIDER` (`smtp` or `ses`)
- SMTP mode:
  - `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- SES mode:
  - `APP_MAIL_SES_REGION`
- Shared:
  - `MAIL_FROM`, `COMPANY_MAIL_TO`

File Storage:

- `APP_STORAGE_PROVIDER` (`s3`)
- S3 mode:
  - `APP_STORAGE_S3_BUCKET`
  - `APP_STORAGE_S3_REGION`
  - `APP_STORAGE_S3_KEY_PREFIX`
  - `APP_STORAGE_S3_PRESIGN_EXPIRATION_SECONDS`

Bootstrap login users:

- `APP_BOOTSTRAP_ADMIN_*`
- `APP_BOOTSTRAP_HR_*`
- `APP_BOOTSTRAP_EMPLOYEE_*`

## Run Locally

1. Start PostgreSQL
2. Configure env vars from `.env.example`
3. Run:

```bash
mvn spring-boot:run
```

Health: `GET http://localhost:8080/api/health`

## API Summary

- `POST /api/careers/applications/presign`
- `POST /api/careers/applications`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/employee/home`
- `GET /api/hr/applications`
- `GET /api/hr/applications/{id}/resume`
- `GET /api/hr/applications/{applicationId}/files/{fileId}`
- `GET /api/admin/users`

## AWS Deployment

Use the full infrastructure and setup guide:

- `../AWS_DEPLOYMENT.md`
- `../infrastructure/terraform/README.md`
