data "aws_ami" "amazon_linux2" {
  count       = var.ec2_ami_id == "" ? 1 : 0
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

resource "aws_instance" "backend" {
  ami                    = var.ec2_ami_id != "" ? var.ec2_ami_id : data.aws_ami.amazon_linux2[0].id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public["a"].id
  vpc_security_group_ids = [aws_security_group.ec2.id]
  iam_instance_profile   = aws_iam_instance_profile.ec2.name
  key_name               = var.key_pair_name != "" ? var.key_pair_name : null

  root_block_device {
    volume_type = "gp3"
    volume_size = 20
    encrypted   = true
  }

  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "required"
  }

  user_data = templatefile("${path.module}/templates/ec2_user_data.sh.tftpl", {
    aws_region                   = var.aws_region
    project_name                 = var.project_name
    environment                  = var.environment
    db_host                      = aws_db_instance.postgres.address
    db_port                      = aws_db_instance.postgres.port
    db_name                      = var.db_name
    db_username                  = var.db_username
    db_password                  = var.db_password
    resume_bucket_name           = aws_s3_bucket.resumes.bucket
    artifact_bucket_name         = aws_s3_bucket.artifacts.bucket
    frontend_urls_csv            = local.frontend_urls_csv
    company_mail_to              = var.company_mail_to
    contact_mail_to              = var.contact_mail_to
    mail_from_address            = var.mail_from_address
    jwt_secret                   = var.jwt_secret
    ses_region                   = var.ses_region
    bootstrap_admin_username     = var.bootstrap_admin_username
    bootstrap_admin_password     = var.bootstrap_admin_password
    bootstrap_admin_full_name    = var.bootstrap_admin_full_name
    bootstrap_admin_email        = var.bootstrap_admin_email
    bootstrap_hr_username        = var.bootstrap_hr_username
    bootstrap_hr_password        = var.bootstrap_hr_password
    bootstrap_hr_full_name       = var.bootstrap_hr_full_name
    bootstrap_hr_email           = var.bootstrap_hr_email
    bootstrap_employee_username  = var.bootstrap_employee_username
    bootstrap_employee_password  = var.bootstrap_employee_password
    bootstrap_employee_full_name = var.bootstrap_employee_full_name
    bootstrap_employee_email     = var.bootstrap_employee_email
    cloudwatch_log_group_backend = aws_cloudwatch_log_group.backend.name
    cloudwatch_log_group_nginx   = aws_cloudwatch_log_group.nginx.name
  })

  depends_on = [
    aws_db_instance.postgres,
    aws_cloudwatch_log_group.backend,
    aws_cloudwatch_log_group.nginx
  ]

  lifecycle {
    ignore_changes = [user_data]
  }
}

resource "aws_eip" "backend" {
  domain   = "vpc"
  instance = aws_instance.backend.id
}

resource "terraform_data" "sync_bootstrap_passwords" {
  triggers_replace = {
    instance_id        = aws_instance.backend.id
    runtime_layout     = "mouw"
    db_host            = aws_db_instance.postgres.address
    db_name            = var.db_name
    db_username        = var.db_username
    admin_username     = var.bootstrap_admin_username
    admin_password     = var.bootstrap_admin_password
    admin_full_name    = var.bootstrap_admin_full_name
    admin_email        = var.bootstrap_admin_email
    hr_username        = var.bootstrap_hr_username
    hr_password        = var.bootstrap_hr_password
    hr_full_name       = var.bootstrap_hr_full_name
    hr_email           = var.bootstrap_hr_email
    employee_username  = var.bootstrap_employee_username
    employee_password  = var.bootstrap_employee_password
    employee_full_name = var.bootstrap_employee_full_name
    employee_email     = var.bootstrap_employee_email
    mail_from_address  = var.mail_from_address
    company_mail_to    = var.company_mail_to
    contact_mail_to    = var.contact_mail_to
  }

  provisioner "local-exec" {
    interpreter = ["bash", "-c"]
    command = <<-EOT
      set -euo pipefail
      INSTANCE_ID="${aws_instance.backend.id}"
      aws ec2 wait instance-running --instance-ids "$INSTANCE_ID"
      for attempt in $(seq 1 36); do
        PING="$(aws ssm describe-instance-information --filters "Key=InstanceIds,Values=$INSTANCE_ID" --query "InstanceInformationList[0].PingStatus" --output text 2>/dev/null || true)"
        if [ "$PING" = "Online" ]; then
          break
        fi
        if [ "$attempt" -eq 36 ]; then
          echo "SSM agent did not come online for $INSTANCE_ID" >&2
          exit 1
        fi
        sleep 10
      done
      PARAMS="$(mktemp)"
      cat > "$PARAMS" <<'EOF'
      ${jsonencode({
    commands = [templatefile("${path.module}/templates/sync_bootstrap_passwords.sh.tftpl", {
      aws_region                   = var.aws_region
      db_host                      = aws_db_instance.postgres.address
      db_port                      = aws_db_instance.postgres.port
      db_name                      = var.db_name
      db_username                  = var.db_username
      db_password                  = var.db_password
      resume_bucket_name           = aws_s3_bucket.resumes.bucket
      frontend_urls_csv            = local.frontend_urls_csv
      company_mail_to              = var.company_mail_to
      contact_mail_to              = var.contact_mail_to
      mail_from_address            = var.mail_from_address
      jwt_secret                   = var.jwt_secret
      ses_region                   = var.ses_region
      bootstrap_admin_username     = var.bootstrap_admin_username
      bootstrap_admin_password     = var.bootstrap_admin_password
      bootstrap_admin_full_name    = var.bootstrap_admin_full_name
      bootstrap_admin_email        = var.bootstrap_admin_email
      bootstrap_hr_username        = var.bootstrap_hr_username
      bootstrap_hr_password        = var.bootstrap_hr_password
      bootstrap_hr_full_name       = var.bootstrap_hr_full_name
      bootstrap_hr_email           = var.bootstrap_hr_email
      bootstrap_employee_username  = var.bootstrap_employee_username
      bootstrap_employee_password  = var.bootstrap_employee_password
      bootstrap_employee_full_name = var.bootstrap_employee_full_name
      bootstrap_employee_email     = var.bootstrap_employee_email
      cloudwatch_log_group_backend = aws_cloudwatch_log_group.backend.name
      cloudwatch_log_group_nginx   = aws_cloudwatch_log_group.nginx.name
    })]
})}
      EOF
      COMMAND_ID="$(aws ssm send-command \
        --instance-ids "${aws_instance.backend.id}" \
        --document-name "AWS-RunShellScript" \
        --comment "Sync portal bootstrap passwords" \
        --timeout-seconds 1500 \
        --parameters "file://$PARAMS" \
        --query "Command.CommandId" \
        --output text)"
      rm -f "$PARAMS"
      deadline=$((SECONDS + 1500))
      while true; do
        STATUS="$(aws ssm get-command-invocation --command-id "$COMMAND_ID" --instance-id "$INSTANCE_ID" --query Status --output text 2>/dev/null || true)"
        case "$STATUS" in
          Success)
            break
            ;;
          Failed|Cancelled|TimedOut|Cancelling)
            echo "Server setup command $COMMAND_ID finished with $STATUS" >&2
            aws ssm get-command-invocation --command-id "$COMMAND_ID" --instance-id "$INSTANCE_ID" --query StandardErrorContent --output text >&2 || true
            exit 1
            ;;
        esac
        if [ "$SECONDS" -ge "$deadline" ]; then
          echo "Server setup command $COMMAND_ID did not finish" >&2
          exit 1
        fi
        sleep 15
      done
    EOT
}
}
