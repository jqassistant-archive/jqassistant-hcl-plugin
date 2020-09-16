variable "all" {
   description = "all description"
   default     = "xyz"
   type        = string
   
   validation {
     condition     = length(var.all) > 0
     error_message = "This is an error."
   }
}