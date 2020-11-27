package com.jorgetrujillo.webfluxdemo.exceptions

data class ErrorResponse(
  val message: String,
  val errors: List<String>
)
