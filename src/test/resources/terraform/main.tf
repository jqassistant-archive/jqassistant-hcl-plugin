module "input-variables" {
  source = "./input variable"
}

module "output-variables" {
  source = "./output variable"
}

provider "aws" {
  region = "eu-central-1"
}