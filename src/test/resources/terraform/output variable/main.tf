resource "aws_db_instance" "db" {
  name           = "a"
  instance_class = "t3.medium"
}

resource "aws_db_instance" "db_new" {
  name           = "b"
  instance_class = "t3.medium"
}