terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
    google = {
      source = "hashicorp/google"
    }
    grafana = {
      source = "grafana/grafana"
    }
    time = {
      source = "hashicorp/time"
    }
  }
  required_version = ">= 0.13"
}
