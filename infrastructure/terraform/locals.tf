locals {
  name_prefix       = "${var.project_name}-${var.environment}"
  use_existing_zone = var.use_custom_domain && var.hosted_zone_id != ""
  api_fqdn          = var.use_custom_domain ? "${var.api_subdomain}.${var.domain_name}" : ""

  frontend_aliases = var.use_custom_domain ? (
    var.create_www_record ? [var.domain_name, "www.${var.domain_name}"] : [var.domain_name]
  ) : []

  # Kept off the CloudFront hostname on purpose. The API origin is the EC2
  # address, so putting the distribution domain here would cycle through user data.
  # Browser API calls are same-origin on CloudFront, so this list is for extra origins only
  # until a custom domain is enabled.
  frontend_urls = distinct(concat(
    var.use_custom_domain ? ["https://${var.domain_name}"] : [],
    var.use_custom_domain && var.create_www_record ? ["https://www.${var.domain_name}"] : [],
    var.extra_frontend_urls
  ))

  frontend_urls_csv = join(",", local.frontend_urls)

  api_origin_domain = var.use_custom_domain ? local.api_fqdn : (
    aws_eip.backend.public_dns != null && aws_eip.backend.public_dns != ""
    ? aws_eip.backend.public_dns
    : "ec2-${replace(aws_eip.backend.public_ip, ".", "-")}.${var.aws_region}.compute.amazonaws.com"
  )

  common_tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}
