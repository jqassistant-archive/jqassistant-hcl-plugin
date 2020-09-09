variable "all" {
   description = "all description"
   default     = "xyz"
   type        = string
   
   validation {
     condition = length(var.all)
     error_message = "error"
   }
}

locals {
  a = "B"
}