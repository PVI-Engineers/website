resource "aws_ses_domain_identity" "domain" {
  provider = aws.ses
  domain   = var.domain_name
}

resource "aws_route53_record" "ses_verification" {
  zone_id         = local.route53_zone_id
  name            = "_amazonses.${var.domain_name}"
  type            = "TXT"
  ttl             = 600
  allow_overwrite = true
  records         = [aws_ses_domain_identity.domain.verification_token]
}

resource "aws_ses_domain_identity_verification" "domain" {
  provider = aws.ses
  domain   = aws_ses_domain_identity.domain.id

  depends_on = [aws_route53_record.ses_verification]
}

resource "aws_ses_domain_dkim" "domain" {
  provider = aws.ses
  domain   = aws_ses_domain_identity.domain.domain
}

resource "aws_route53_record" "ses_dkim" {
  count           = 3
  zone_id         = local.route53_zone_id
  name            = "${aws_ses_domain_dkim.domain.dkim_tokens[count.index]}._domainkey.${var.domain_name}"
  type            = "CNAME"
  ttl             = 600
  allow_overwrite = true
  records         = ["${aws_ses_domain_dkim.domain.dkim_tokens[count.index]}.dkim.amazonses.com"]
}
