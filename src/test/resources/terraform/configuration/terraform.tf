terraform {
  backend "s3" {
    bucket = "mybucket"
    key    = "path/to/my/key"
    region = "us-east-1"
  }
  
  required_version = ">=1.2.0"
  
  required_providers {
    aws = {
      version = ">= 2.7.0"
      source = "hashicorp/aws"
    }
  }
  
  experiments = [example]
  
  provider_meta "my-provider" {
    hello = "world"
  }
}