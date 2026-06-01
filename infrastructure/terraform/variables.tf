variable "aws_region" {
  description = "Primary AWS region for VPC, EC2, RDS, S3, CloudFront origin resources."
  type        = string
  default     = "us-west-2"
}

variable "project_name" {
  description = "Project identifier used in resource names and tags."
  type        = string
  default     = "pvi"
}

variable "environment" {
  description = "Deployment environment name."
  type        = string
  default     = "prod"
}

variable "domain_name" {
  description = "Primary public apex DNS domain without www (example: pviengineers.com)."
  type        = string

  validation {
    condition     = !startswith(lower(var.domain_name), "www.")
    error_message = "domain_name must be the apex domain without 'www.' (example: pviengineers.com)."
  }
}

variable "hosted_zone_id" {
  description = "Existing Route53 Hosted Zone ID. Leave empty to create a new hosted zone."
  type        = string
  default     = ""
}

variable "api_subdomain" {
  description = "Subdomain for backend origin (used internally by CloudFront origin routing)."
  type        = string
  default     = "api"
}

variable "frontend_bucket_name" {
  description = "S3 bucket name for static frontend hosting."
  type        = string
}

variable "resume_bucket_name" {
  description = "S3 bucket name for application resume storage."
  type        = string
}

variable "artifacts_bucket_name" {
  description = "S3 bucket name for CI/CD backend build artifacts."
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for the application VPC."
  type        = string
  default     = "10.40.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDRs for public subnets (minimum two, different AZs)."
  type        = list(string)
  default     = ["10.40.1.0/24", "10.40.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDRs for private subnets (minimum two, different AZs)."
  type        = list(string)
  default     = ["10.40.11.0/24", "10.40.12.0/24"]
}

variable "instance_type" {
  description = "EC2 instance type for backend server."
  type        = string
  default     = "t2.micro"
}

variable "key_pair_name" {
  description = "Optional EC2 key pair name for SSH access."
  type        = string
  default     = ""
}

variable "admin_ingress_cidr" {
  description = "CIDR block allowed to SSH into EC2."
  type        = string
  default     = "0.0.0.0/0"
}

variable "enable_ssh_ingress" {
  description = "Whether to expose SSH ingress on the backend EC2 security group."
  type        = bool
  default     = false
}

variable "ec2_ami_id" {
  description = "Optional custom AMI ID. Leave empty to use latest Amazon Linux 2."
  type        = string
  default     = ""
}

variable "db_name" {
  description = "RDS PostgreSQL database name."
  type        = string
  default     = "pvi_careers"
}

variable "db_username" {
  description = "RDS PostgreSQL master username."
  type        = string
  default     = "pvi_admin"
}

variable "db_password" {
  description = "RDS PostgreSQL master password."
  type        = string
  sensitive   = true

  validation {
    condition = (
      length(var.db_password) >= 8 &&
      length(var.db_password) <= 128 &&
      can(regex("^[!-~]+$", var.db_password)) &&
      !can(regex("[/@\"]", var.db_password))
    )
    error_message = "db_password must be 8-128 printable ASCII characters and must not contain '/', '@', '\"', or spaces (AWS RDS restriction)."
  }
}

variable "db_instance_class" {
  description = "RDS instance class."
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Initial RDS storage in GB."
  type        = number
  default     = 20
}

variable "mail_from_address" {
  description = "SES verified sender email address."
  type        = string
}

variable "company_mail_to" {
  description = "Company recipient email for incoming application notifications."
  type        = string
}

variable "ses_region" {
  description = "AWS SES region where identities are created and email is sent."
  type        = string
  default     = "us-west-2"
}

variable "jwt_secret" {
  description = "JWT signing secret for backend auth."
  type        = string
  sensitive   = true
}

variable "bootstrap_admin_username" {
  description = "Bootstrap admin login username."
  type        = string
  default     = "admin@pvi.local"
}

variable "bootstrap_admin_password" {
  description = "Bootstrap admin login password."
  type        = string
  sensitive   = true
  default     = "Admin@123"
}

variable "bootstrap_admin_full_name" {
  description = "Bootstrap admin full name."
  type        = string
  default     = "PVI Admin"
}

variable "bootstrap_admin_email" {
  description = "Bootstrap admin email."
  type        = string
  default     = "admin@pviengineers.com"
}

variable "bootstrap_hr_username" {
  description = "Bootstrap HR login username."
  type        = string
  default     = "hr@pvi.local"
}

variable "bootstrap_hr_password" {
  description = "Bootstrap HR login password."
  type        = string
  sensitive   = true
  default     = "Hr@123"
}

variable "bootstrap_hr_full_name" {
  description = "Bootstrap HR full name."
  type        = string
  default     = "PVI HR"
}

variable "bootstrap_hr_email" {
  description = "Bootstrap HR email."
  type        = string
  default     = "hr@pviengineers.com"
}

variable "bootstrap_employee_username" {
  description = "Bootstrap employee login username."
  type        = string
  default     = "employee@pvi.local"
}

variable "bootstrap_employee_password" {
  description = "Bootstrap employee login password."
  type        = string
  sensitive   = true
  default     = "Employee@123"
}

variable "bootstrap_employee_full_name" {
  description = "Bootstrap employee full name."
  type        = string
  default     = "PVI Employee"
}

variable "bootstrap_employee_email" {
  description = "Bootstrap employee email."
  type        = string
  default     = "employee@pviengineers.com"
}

variable "extra_frontend_urls" {
  description = "Additional frontend origins for CORS (e.g., staging domains)."
  type        = list(string)
  default     = []
}

variable "alarm_email" {
  description = "Optional email address for CloudWatch alarm notifications. Leave blank to disable SNS subscription."
  type        = string
  default     = ""
}

variable "create_www_record" {
  description = "Whether to create www DNS alias record."
  type        = bool
  default     = true
}

variable "rds_skip_final_snapshot" {
  description = "Whether to skip final snapshot on RDS deletion."
  type        = bool
  default     = true
}

variable "rds_deletion_protection" {
  description = "Enable RDS deletion protection to prevent accidental destruction."
  type        = bool
  default     = true
}

variable "enable_waf" {
  description = "Attach AWS WAF to CloudFront distribution."
  type        = bool
  default     = true
}

variable "waf_rate_limit" {
  description = "Requests per 5-minute window per source IP before WAF rate-based blocking."
  type        = number
  default     = 2000
}

variable "github_repository" {
  description = "GitHub repository in owner/repo format for OIDC-based CI/CD role. Leave empty to skip role creation."
  type        = string
  default     = ""
}

variable "github_branch" {
  description = "GitHub branch allowed to assume deploy role."
  type        = string
  default     = "main"
}
