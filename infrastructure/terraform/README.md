# AWS Terraform Stack - MOUW Engineering and Land Surveyors

This Terraform stack provisions the complete production-grade AWS setup requested:

- EC2 (`t2.micro`) for backend API runtime
- RDS PostgreSQL (private subnets)
- S3 (frontend hosting origin, resume storage, CI artifacts)
- CloudFront (frontend delivery + `/api/*` proxy to EC2 origin)
- Optional Route53 DNS and ACM certificate when `use_custom_domain` is true
- Optional SES domain identity + DKIM records when `use_custom_domain` is true
- CloudWatch logs + alarms
- IAM roles/policies for runtime and deployment

## Architecture

- By default the public URL is the CloudFront distribution domain (`https://xxxx.cloudfront.net`), using CloudFront's default certificate. Route53 and ACM are not created.
- Set `use_custom_domain = true` later to attach an apex domain, `www`, Route53, and an ACM certificate.
- CloudFront serves static Vue files from S3.
- CloudFront forwards `/api/*` calls to the EC2 public DNS name. With a custom domain, that origin is `api.<domain>` instead.
- Spring Boot on EC2 connects to RDS and issues pre-signed S3 upload URLs.
- Browser uploads resume/supporting files directly to S3 using pre-signed URLs.
- Spring Boot sends emails via AWS SES API.
- CloudWatch collects backend and nginx logs.

## Prerequisites

- Terraform 1.7+
- AWS CLI authenticated with account permissions
- A registered domain name is required only when `use_custom_domain` is true

## Usage

1. Copy `terraform.tfvars.example` to `terraform.tfvars`
   - or start from `terraform.tfvars.mouw-draft.example` (pre-filled for this project naming)
2. Fill real values (bucket names, DB password, JWT secret, etc.)
   - Leave `use_custom_domain = false` to use the CloudFront domain. Open the `frontend_url` output after apply.
   - When you have a domain, set `use_custom_domain = true` and `domain_name` to the apex without `www`
   - `db_password` must be 8-128 printable ASCII chars and cannot include `/`, `@`, `"`, or spaces (AWS RDS rule)
   - Add any extra frontend origins (for staging/local) in `extra_frontend_urls` so S3 upload CORS allows them
   - Keep `enable_ssh_ingress=false` unless you need temporary SSH access
3. Run:

```bash
terraform init \
  -backend-config="bucket=<your-tf-state-bucket>" \
  -backend-config="key=<path/to/terraform.tfstate>" \
  -backend-config="region=<aws-region>" \
  -backend-config="use_lockfile=true" \
  -backend-config="encrypt=true"
terraform plan
terraform apply
```

4. With `use_custom_domain = false`, skip DNS. The site URL is output `frontend_url` (the CloudFront domain).
   An existing hosted zone is left in Route 53 and is not deleted.
   With `use_custom_domain = true` and an empty `hosted_zone_id`, Terraform creates a hosted zone.
   Point the registrar at output `route53_name_servers`, then wait for ACM and SES DNS verification.

5. SES domain verification is created only when `use_custom_domain` is true. Until then, application and contact emails are not sent through a verified domain.

## Cloud-Only Terraform (No Local Run)

Use workflow `.github/workflows/infra-terraform.yml` and set these GitHub secrets:

- `AWS_ROLE_TO_ASSUME_INFRA`
- `AWS_REGION`
- `TF_STATE_BUCKET`
- `TF_STATE_KEY`
- `TF_VARS_CONTENT` (paste full terraform vars content)

The workflow bootstraps Terraform state resources, then runs init/plan/apply in GitHub Actions.

## GitHub OIDC CI/CD Role

- Set `github_repository = "your-org/your-repo"` in `terraform.tfvars`.
- Terraform will create `github_actions_role_arn` output.
- Use that ARN in GitHub secret: `AWS_ROLE_TO_ASSUME_APP`.
- Make sure your AWS account already has OIDC provider:
  `token.actions.githubusercontent.com`.

## Important Outputs

- `frontend_url`
- `cloudfront_distribution_id`
- `frontend_bucket_name`
- `artifacts_bucket_name`
- `ec2_instance_id`
- `api_fqdn`
- `rds_endpoint`

## Cost Notes

- EC2 uses `t2.micro` as requested.
- RDS uses `db.t3.micro` (smallest practical Postgres class).
- CloudFront and S3 are usage-based. Route53 is created only when `use_custom_domain` is true.

## Security Notes

- WAF (managed rule groups + rate limiting) is enabled by default for CloudFront.
- CloudFront applies strict security headers (HSTS, CSP, frame denial, referrer policy).
- S3 buckets enforce TLS-only requests and keep public access blocked.
- RDS enforces SSL connections (`rds.force_ssl=1`) and enables deletion protection by default.
- SSH ingress is disabled by default (`enable_ssh_ingress=false`); enable only when needed.
- Keep SSH CIDR narrow (`admin_ingress_cidr`) in production.
- Rotate bootstrap passwords after first login.
- Move sensitive values to Terraform Cloud workspace vars or a secure secret store.
