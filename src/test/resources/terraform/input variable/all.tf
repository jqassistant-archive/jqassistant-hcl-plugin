variable "all" {
   description = "all description"
   default     = "xyz"
   type        = string
   
   validation {
     condition = length(var.all) = 7
     error_message = "String too long."
   }
}
