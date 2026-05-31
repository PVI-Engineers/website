locals {
  name_prefix       = "${var.project_name}-${var.environment}"
  use_existing_zone = var.hosted_zone_id != ""
  api_fqdn          = "${var.api_subdomain}.${var.domain_name}"

  frontend_aliases = var.create_www_record ? [var.domain_name, "www.${var.domain_name}"] : [var.domain_name]

  frontend_urls = distinct(concat(
    [
      "https://${var.domain_name}",
    ],
    var.create_www_record ? ["https://www.${var.domain_name}"] : [],
    var.extra_frontend_urls
  ))

  frontend_urls_csv = join(",", local.frontend_urls)

  common_tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}
