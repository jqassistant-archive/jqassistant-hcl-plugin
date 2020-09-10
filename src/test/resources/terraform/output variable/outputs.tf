output "db_password" {
  value       = aws_db_instance.db.password
  description = "The password for logging in to the database."
  sensitive   = true
  
  depends_on = [
    aws_db_instance.db
  ]
}
