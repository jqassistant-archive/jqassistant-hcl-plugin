data "google_billing_account" "account" {
  billing_account = "123456-0000000-0000000-000000"
}

resource "grafana_data_source" "influxdb" {
  type          = "influxdb"
  name          = "test_influxdb"
  url           = "http://influxdb.example.net:8086/"
  username      = "foo"
  password      = "bar"
  database_name = "mydb"
}

resource "time_static" "ami_update" {
  triggers = {
    # Save the time each switch of an AMI id
    ami_id = data.aws_ami.ami.id
  }
}