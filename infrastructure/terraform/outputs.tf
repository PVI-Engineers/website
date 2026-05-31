output "vpc_id" {
  value = aws_vpc.main.id
}

output "ec2_instance_id" {
  value = aws_instance.backend.id
}

output "ec2_public_ip" {
  value = aws_eip.backend.public_ip
}

output "api_fqdn" {
  value = local.api_fqdn
}

output "cloudfront_distribution_id" {
  value = aws_cloudfront_distribution.site.id
}

output "cloudfront_domain_name" {
  value = aws_cloudfront_distribution.site.domain_name
}

output "frontend_url" {
  value = "https://${var.domain_name}"
}

output "frontend_bucket_name" {
  value = aws_s3_bucket.frontend.bucket
}

output "resume_bucket_name" {
  value = aws_s3_bucket.resumes.bucket
}

output "artifacts_bucket_name" {
  value = aws_s3_bucket.artifacts.bucket
}

output "rds_endpoint" {
  value = aws_db_instance.postgres.address
}

output "route53_zone_id" {
  value = local.route53_zone_id
}

output "route53_name_servers" {
  value       = local.use_existing_zone ? [] : aws_route53_zone.primary[0].name_servers
  description = "If a new hosted zone was created, point your registrar to these name servers."
}

output "github_actions_role_arn" {
  value       = var.github_repository != "" ? aws_iam_role.github_actions_deploy[0].arn : ""
  description = "Use this as AWS_ROLE_TO_ASSUME_APP in GitHub Secrets."
}
