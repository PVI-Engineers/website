# PVI Engineers - Full AWS Deployment Guide

This guide matches your requested stack:

- EC2 (`t2.micro`) for backend runtime
- RDS PostgreSQL
- S3 (frontend hosting + resumes + build artifacts)
- CloudFront
- Route53 DNS
- ACM free SSL certificate
- SES for email
- CloudWatch logging/alarms
- CI/CD via GitHub Actions
- Terraform for full infrastructure

## 1) Infrastructure Code Location

- Terraform: `infrastructure/terraform`
- CI/CD workflow: `.github/workflows/aws-cicd.yml`
- Infra workflow (cloud-only): `.github/workflows/infra-terraform.yml`

## 2) Replace These Values First

Update these placeholders with your real values:

- `infrastructure/terraform/terraform.tfvars.pvi-draft.example`
  - `REPLACE_GITHUB_ORG/REPLACE_REPO`
  - `REPLACE_EC2_KEYPAIR_NAME`
  - `REPLACE_YOUR_PUBLIC_IP/32`
  - `REPLACE_WITH_STRONG_DB_PASSWORD`
  - `REPLACE_WITH_LONG_RANDOM_JWT_SECRET_MIN_32_CHARS`
  - `REPLACE_ADMIN_PASSWORD`
  - `REPLACE_HR_PASSWORD`
  - `REPLACE_EMPLOYEE_PASSWORD`
  - (Optional) add local/staging origins in `extra_frontend_urls` for S3 upload CORS

- `AWS_DEPLOYMENT.md` command placeholders (when running fallback manual deploy):
  - `<ARTIFACT_BUCKET>`
  - `<EC2_INSTANCE_ID>`
  - `<your-domain>`

- GitHub Secrets (required):
  - `AWS_ROLE_TO_ASSUME_INFRA`
  - `AWS_ROLE_TO_ASSUME_APP`
  - `AWS_REGION`
  - `TF_STATE_BUCKET`
  - `TF_STATE_KEY`
  - `TF_LOCK_TABLE`
  - `TF_VARS_CONTENT`
  - `FRONTEND_BUCKET`
  - `ARTIFACT_BUCKET`
  - `CLOUDFRONT_DISTRIBUTION_ID`
  - `EC2_INSTANCE_ID`

## 3) Backend Changes Made for AWS

- Added AWS SDK support for:
  - S3 pre-signed upload URLs (direct browser-to-S3 uploads)
  - SES mail sending (raw email, attachment support)
- Added direct-upload application flow:
  1. Frontend requests upload URLs from backend
  2. Frontend uploads files directly to S3
  3. Frontend submits application JSON with uploaded S3 keys
- Added runtime storage provider:
  - `APP_STORAGE_PROVIDER=s3` (required for pre-signed uploads)
- Added runtime mail provider toggle:
  - `APP_MAIL_PROVIDER=smtp` (local Gmail SMTP)
  - `APP_MAIL_PROVIDER=ses` (AWS production)
- Added `resumeStorageKey` in DB model to reference S3 object key.

## 4) Frontend Changes for CloudFront

Frontend API clients now support same-domain API mode:

- Set `VITE_API_BASE_URL=` (empty) for production CloudFront deployment.
- API calls become `/api/...` (same origin from CloudFront).

## 5) Cloud-Only Infra Deploy (No Local Terraform)

You can deploy infra directly from GitHub Actions.

Set these GitHub secrets first:

- `AWS_ROLE_TO_ASSUME_INFRA`
- `AWS_REGION`
- `TF_STATE_BUCKET`
- `TF_STATE_KEY` (example: `pvi/prod/terraform.tfstate`)
- `TF_LOCK_TABLE` (example: `pvi-prod-tf-locks`)
- `TF_VARS_CONTENT` (full content of your `terraform.tfvars`)

`AWS_ROLE_TO_ASSUME_INFRA` should be a high-privilege infra role (or admin role) that can create VPC, EC2, RDS, CloudFront, Route53, IAM, S3, SES, and CloudWatch resources.

Then trigger workflow:

- GitHub -> Actions -> `infra-terraform`
- Choose mode `apply` and run

The workflow automatically:

1. Creates/uses Terraform state bucket and lock table
2. Runs `terraform init/plan/apply`
3. Publishes key outputs in the workflow summary

## 6) Optional Local Infra Deploy (if needed)

From `infrastructure/terraform`:

```bash
terraform init \
  -backend-config="bucket=<your-tf-state-bucket>" \
  -backend-config="key=<path/to/terraform.tfstate>" \
  -backend-config="region=<aws-region>" \
  -backend-config="dynamodb_table=<your-lock-table>" \
  -backend-config="encrypt=true"
copy terraform.tfvars.pvi-draft.example terraform.tfvars
terraform plan
terraform apply
```

Fill `terraform.tfvars` carefully:

- domain + bucket names
  - set `domain_name` to apex only (for your case: `pviengineers.com`, not `www.pviengineers.com`)
- DB password
- JWT secret
- SES sender and company recipient email

After infra apply, do a normal git push to `main` with app code.  
Workflow `aws-cicd` will build frontend/backend in GitHub and deploy to AWS (no local run needed).

Optional manual fallback deployment (only if you don't want CI for first deploy):

```bash
aws s3 cp pvi-careers-api/target/pvi-careers-api-0.0.1-SNAPSHOT.jar s3://<ARTIFACT_BUCKET>/backend/pvi-careers-api.jar
aws ssm send-command \
  --document-name "AWS-RunShellScript" \
  --instance-ids "<EC2_INSTANCE_ID>" \
  --parameters commands="sudo /usr/local/bin/deploy-backend.sh <ARTIFACT_BUCKET> backend/pvi-careers-api.jar"
```

## 7) DNS + SSL Notes

- If Terraform creates a new hosted zone, update your registrar name servers with output `route53_name_servers`.
- ACM certificate is auto-created in `us-east-1` for CloudFront.
- CloudFront aliases are configured for root domain (+ `www` when enabled).

## 8) SES Setup

Terraform creates:

- SES domain identity
- SES DKIM records in Route53

After DNS propagates, SES should verify domain automatically.
Make sure `mail_from_address` belongs to verified domain.

## 9) CI/CD Setup (GitHub)

To auto-create a GitHub deploy role via Terraform:

- set `github_repository = "your-org/your-repo"` in `terraform.tfvars`
- apply Terraform and read output `github_actions_role_arn`
- use that output as `AWS_ROLE_TO_ASSUME_APP`

Create these GitHub Secrets:

- `AWS_ROLE_TO_ASSUME_APP` (OIDC role ARN from `github_actions_role_arn` output)
- `AWS_REGION`
- `FRONTEND_BUCKET`
- `ARTIFACT_BUCKET`
- `CLOUDFRONT_DISTRIBUTION_ID`
- `EC2_INSTANCE_ID`

Pipeline flow:

1. Build frontend and backend
2. Upload frontend `dist` to S3
3. Invalidate CloudFront
4. Upload backend jar to artifact bucket
5. Use SSM command to deploy jar on EC2 with SHA-256 verification and restart service

## 10) EC2 Runtime Behavior

User-data config installs:

- Java 17
- Nginx
- CloudWatch Agent
- Systemd service for Spring Boot
- Deployment helper script: `/usr/local/bin/deploy-backend.sh`

Nginx receives `/api/*` and proxies to Spring Boot on port `8080`.

## 11) Post Deployment Validation

1. Open `https://<your-domain>`
2. Check API:
   - `https://<your-domain>/api/health`
3. Submit careers application form with resume + optional supporting files
4. Validate:
   - row inserted in RDS
   - uploaded files present in S3 resume bucket
   - company email received (SES)
   - applicant thank-you email received
5. Check logs:
   - CloudWatch log groups `/pvi-prod/backend` and `/pvi-prod/nginx`

## 12) Production Hardening (recommended)

- Restrict `admin_ingress_cidr` to office/public static IP only.
- Rotate bootstrap passwords and create managed admin accounts.
- Move secrets to AWS Secrets Manager / SSM Parameter Store.
- Enable WAF on CloudFront if public attack surface grows.

Additional hardening now included in Terraform:

- CloudFront-attached AWS WAF (managed rules + IP rate limiting)
- CloudFront security headers policy (HSTS, CSP, frame deny, strict referrer)
- S3 TLS-only bucket policies (frontend/resume/artifacts buckets)
- RDS SSL enforcement (`rds.force_ssl=1`) and deletion protection toggle
- EC2 IMDSv2 enforcement (`http_tokens=required`)
- Optional SSH exposure control (`enable_ssh_ingress`, default disabled)
