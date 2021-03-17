data "aws_vpc" "vpc" {
  default = true
}

resource "aws_security_group" "test" {
  name = "egress example"
  
  ingress {
    description = "TLS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}