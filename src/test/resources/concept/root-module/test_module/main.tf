variable "in" {
  type = string
}

resource "aws_db_instance" "main" {
  instance_class = "t3.medium"
}

output "out" {
  value = aws_db_instance.main.arn
}
