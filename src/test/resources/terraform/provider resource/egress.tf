data "aws_vpc" "vpc" {
  default = true
}

resource "aws_security_group" "test" {
  name = "egress example"
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}