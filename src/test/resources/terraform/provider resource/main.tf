resource "aws_eip" "eip" {
  instance = aws_instance.server.id
  vpc      = true
}

resource "aws_security_group" "server" {
  name        = "server-security-group"
  description = "my security group"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "aws_ami" "ami" {
  most_recent = true
  name_regex  = "my-ami"
  owners      = ["self"]
}

resource "aws_instance" "server" {
  instance_type = "t2.micro"

  ami = data.aws_ami.ami.id

  security_groups = [aws_security_group.server.name]

  tags = {
    Name = "my server"
  }
}

provider "aws" {
  region = "eu-central-1"
}