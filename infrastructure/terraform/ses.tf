resource "aws_ses_domain_identity" "domain" {
  count    = var.use_custom_domain ? 1 : 0
  provider = aws.ses
  domain   = var.domain_name
}

resource "aws_route53_record" "ses_verification" {
  count           = var.use_custom_domain ? 1 : 0
  zone_id         = local.route53_zone_id
  name            = "_amazonses.${var.domain_name}"
  type            = "TXT"
  ttl             = 600
  allow_overwrite = true
  records         = [aws_ses_domain_identity.domain[0].verification_token]
}

resource "aws_ses_domain_identity_verification" "domain" {
  count    = var.use_custom_domain ? 1 : 0
  provider = aws.ses
  domain   = aws_ses_domain_identity.domain[0].id

  depends_on = [aws_route53_record.ses_verification]
}

resource "aws_ses_domain_dkim" "domain" {
  count    = var.use_custom_domain ? 1 : 0
  provider = aws.ses
  domain   = aws_ses_domain_identity.domain[0].domain
}

resource "aws_route53_record" "ses_dkim" {
  count           = var.use_custom_domain ? 3 : 0
  zone_id         = local.route53_zone_id
  name            = "${aws_ses_domain_dkim.domain[0].dkim_tokens[count.index]}._domainkey.${var.domain_name}"
  type            = "CNAME"
  ttl             = 600
  allow_overwrite = true
  records         = ["${aws_ses_domain_dkim.domain[0].dkim_tokens[count.index]}.dkim.amazonses.com"]
}
