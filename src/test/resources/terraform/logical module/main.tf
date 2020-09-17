variable "a" {
  type = string
}

output "b" {
  value = aws_db_instance.db.arn
}