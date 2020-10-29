variable "a" {
  type = string
}

output "b" {
  value = aws_db_instance.db.arn
}

resource "aws_db_instance" "db" {
  name           = "b"
  instance_class = "t3.medium"
}

provider "aws" {
  region = "eu-central-1"
}