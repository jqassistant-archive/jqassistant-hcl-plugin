data "google_billing_account" "account" {
  billing_account = "123456-7891234-5678901-234567"
}

resource "grafana_user" "user" {
  email    = "user@test.com"
  name     = "name"
  login    = "user"
  password = "password"
}

resource "time_static" "ami_update" {
  triggers = {
    # Save the time each switch of an AMI id
    ami_id = data.aws_ami.ami.id
  }
}

provider "grafana" {
  url  = "http://localhost/"
  auth = "12345"
}