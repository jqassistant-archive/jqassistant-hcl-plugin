# the filename contains a hyphen and the name is mapped to 'internalName' in the graph
provider "aws" {
  region = "eu-central-1"
}

resource "aws_db_instance" "valid_object_name" {
  instance_class = "t3.medium"
}