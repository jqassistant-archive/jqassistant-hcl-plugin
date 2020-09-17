module "local_count" {
  source     = "./test_module"
  depends_on = [aws_db_instance.main]
  count      = 2
  
  in = "4711"
}

module "local_foreach" {
  source   = "./test_module"
  for_each = toset(["assets", "media"])
  
  in = "${each.key}_4712"
}

module "remote" {
  source  = "hashicorp/nomad/aws"
  version = "0.6.7"
}
   
resource "aws_db_instance" "main" {
  instance_class = "t3.medium"
}

provider "aws" {
  region = "eu-central-1"
}