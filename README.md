# website

# PVI Engineers

aws_region   = "us-west-2"
project_name = "pvi"
environment  = "prod"

github_repository = "PVI-Engineers/website"
github_branch     = "main"

# Keep blank because your current Terraform state already manages the hosted zone.
domain_name    = "pviengineers.com"
hosted_zone_id = ""
api_subdomain  = "api"

# Keep these exactly as current state resource names.
frontend_bucket_name  = "pvi-engineers-prod-frontend"
resume_bucket_name    = "pvi-engineers-prod-resumes"
artifacts_bucket_name = "pvi-engineers-prod-artifacts"

# SSH disabled for now (recommended). No keypair needed.
key_pair_name      = ""
admin_ingress_cidr = "0.0.0.0/0"
enable_ssh_ingress = false
instance_type      = "t2.micro"

db_instance_class    = "db.t3.micro"
db_allocated_storage = 20
db_name              = "pvi_careers"
db_username          = "pvi_admin"

# IMPORTANT: RDS db_password must NOT contain /  @  "  or spaces.
db_password = "PviDb#2026Secure1"

mail_from_address = "no-reply@pviengineers.com"
company_mail_to   = "hr@pviengineers.com"
ses_region        = "us-west-2"

jwt_secret = "PVI_Prod_JWT_2026_kQ9vN7rT2mL5xC8aD4sH1eF6uB3p"

bootstrap_admin_username  = "admin@pvi.local"
bootstrap_admin_password  = "Admin#2026Secure"
bootstrap_admin_full_name = "PVI Admin"
bootstrap_admin_email     = "admin@pviengineers.com"

bootstrap_hr_username  = "hr@pvi.local"
bootstrap_hr_password  = "Hr#2026Secure"
bootstrap_hr_full_name = "PVI HR"
bootstrap_hr_email     = "hr@pviengineers.com"

bootstrap_employee_username  = "employee@pvi.local"
bootstrap_employee_password  = "Employee#2026Secure"
bootstrap_employee_full_name = "PVI Employee"
bootstrap_employee_email     = "employee@pviengineers.com"

extra_frontend_urls = []

alarm_email = ""

create_www_record       = true
rds_skip_final_snapshot = true
rds_deletion_protection = true
enable_waf              = true
waf_rate_limit          = 2000



ACCOUNT VERIFICATION ERROR RESOLUTION

ROOT CAUSE The error indicates that your AWS account requires verification before CloudFront resources can be created. This is a security measure AWS implements for new accounts or accounts that haven't previously used CloudFront services.

RESOLUTION STEPS

STEP 1: VERIFY ACCOUNT INFORMATION Navigate to the AWS Account Settings in the AWS Management Console and ensure all account information is complete and accurate:

Billing information and payment method
Contact information including phone number
Account verification status
STEP 2: COMPLETE IDENTITY VERIFICATION Access the AWS Account and Billing Console to check if additional identity verification is required:

Verify phone number through SMS or voice call
Confirm email address if not already verified
Ensure billing address matches payment method
STEP 3: CHECK SERVICE LIMITS AND QUOTAS Review your account's service limits in the Service Quotas console:

Check CloudFront distribution limits
Verify if any quota increase requests are pending
Ensure account is in good standing
STEP 4: ALTERNATIVE VERIFICATION METHODS Try these alternative approaches:

Use AWS CLI or SDK to create a simple CloudFront distribution first
Create other AWS resources to establish account activity
Ensure account has been active for the minimum required period
STEP 5: BILLING VERIFICATION Confirm your billing setup is complete:

Valid payment method on file
No outstanding billing issues
Account not in suspended status
STEP 6: WAIT FOR AUTOMATIC VERIFICATION Some accounts require a waiting period (typically 24-48 hours) after initial setup before CloudFront resources can be created.

TERRAFORM WORKAROUND While waiting for verification, you can:

Comment out CloudFront resources temporarily
Use conditional resource creation in Terraform
Deploy other infrastructure components first
If these steps do not resolve the verification issue within 48 hours, the account may require manual review through AWS's internal verification process.